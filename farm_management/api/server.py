 feature/python-rest-api-wrapper
import os
from datetime import timedelta
from flask import Flask, jsonify, g
from flask_cors import CORS

# Local application imports
from .auth import token_required, init_app as init_auth_app, generate_jwt
from .farm_routes import register_farm_routes
from .flock_routes import register_flock_routes
from .tracking_routes import register_tracking_routes
from .environment_routes import register_environment_routes # Import for environment routes
from .weather_api import register_weather_routes # Import for weather routes

# Import repository classes (moved here for better organization)
from farm_management.repositories import FarmRepository, FlockRepository, TrackingRepository

def create_app():
    """
    Application factory to create and configure the Flask app.
    Initializes repositories and makes them available.
    """
    app = Flask(__name__)

    # Initialize CORS; allow all origins for now
    CORS(app) # By default, allows all origins with basic methods.

    # Initialize authentication
    # SECRET_KEY and JWT_EXPIRATION_DELTA are now set with defaults in init_auth_app if not present,
    # but it's good practice to define them here or load from environment variables.
    app.config['SECRET_KEY'] = os.environ.get('FLASK_SECRET_KEY', 'your-default-secret-key-CHANGE-ME')
    app.config['JWT_EXPIRATION_DELTA'] = timedelta(hours=int(os.environ.get('JWT_EXP_HOURS', 1)))

    # Example static user for login - replace with a proper user system
    app.config['DEFAULT_API_USER'] = {
        'username': os.environ.get('API_USER_USERNAME', 'apiuser'),
        'password': os.environ.get('API_USER_PASSWORD', 'apipassword123'), # HASH PASSWORDS in real app
        'id': 'user123-static'
    }
    init_auth_app(app) # init_auth_app will use these configs if set, or its own defaults

    # Instantiate repositories
    # These will be available through app.extensions or app.config if preferred,
    # or simply attached to the app object. For simplicity, let's attach to app directly.
    # A more robust way for larger apps might involve Flask extensions or a dedicated service manager.
    app.farm_repo = FarmRepository()
    app.flock_repo = FlockRepository()
    app.tracking_repo = TrackingRepository()

    # Example of how to access repositories in a request context using Flask's 'g' object
    # This is optional if routes directly use current_app.farm_repo etc.
    # Using 'g' can be useful if you want to ensure they are accessed per-request or do setup/teardown.
    # For simple singleton repositories initialized at app start, direct access via current_app is fine.
    # @app.before_request
    # def before_request_func():
    #     g.farm_repo = current_app.farm_repo # Example: access via g
    #     g.flock_repo = current_app.flock_repo
    #     g.tracking_repo = current_app.tracking_repo


    # Basic root route for testing if the server is up
    @app.route('/api/health', methods=['GET'])
    @token_required # Protect this endpoint
    def health_check():
        # Example of accessing repo via current_app (if needed by health check)
        # farm_count = len(current_app.farm_repo.get_all_farms())
        # return jsonify({"status": "healthy", "message": f"Farm Management API is running! Farms: {farm_count}"}), 200
        return jsonify({"status": "healthy", "message": "Farm Management API is running!"}), 200

    # Register Blueprints
    from .farm_routes import register_farm_routes
    register_farm_routes(app)

    from .flock_routes import register_flock_routes
    register_flock_routes(app)

    from .tracking_routes import register_tracking_routes
    register_tracking_routes(app)
    register_environment_routes(app) # Register environment routes blueprint
    register_weather_routes(app) # Register weather routes blueprint

import os
from datetime import timedelta, datetime
import uuid
from flask import Flask, jsonify, g, request, current_app
from flask_cors import CORS
from werkzeug.security import generate_password_hash, check_password_hash

# Local application imports
from .auth import token_required, init_app as init_auth_app, generate_jwt
from .farm_routes import register_farm_routes
from .flock_routes import register_flock_routes
from .tracking_routes import register_tracking_routes
from .environment_routes import register_environment_routes
from .weather_api import register_weather_routes

# Import repository classes
from farm_management.repositories import FarmRepository, FlockRepository, TrackingRepository

# --- User Model (In-Memory for now) ---
# In a real app, this would be a database model.
class User:
    def __init__(self, user_id, username, password_hash, full_name=None, roles=None):
        self.user_id = user_id
        self.username = username
        self.password_hash = password_hash
        self.full_name = full_name
        self.roles = roles if roles is not None else ["farmer"] # Default role

    def to_dict(self):
        return {
            "user_id": self.user_id,
            "username": self.username,
            "full_name": self.full_name,
            "roles": self.roles
        }

def create_app():
    app = Flask(__name__)
    CORS(app)

    # --- Configuration ---
    # Centralized configuration loading
    app.config['SECRET_KEY'] = os.environ.get('FLASK_SECRET_KEY', 'a-very-strong-dev-secret-key-please-change')
    app.config['JWT_EXPIRATION_DELTA'] = timedelta(hours=int(os.environ.get('JWT_EXP_HOURS', 1)))
    # For refresh tokens, if implemented:
    # app.config['JWT_REFRESH_EXPIRATION_DELTA'] = timedelta(days=int(os.environ.get('JWT_REFRESH_EXP_DAYS', 30)))

    if app.config['SECRET_KEY'] == 'a-very-strong-dev-secret-key-please-change' and not app.debug:
        app.logger.warning("SECURITY WARNING: Using default FLASK_SECRET_KEY in a non-debug environment. This is INSECURE.")

    init_auth_app(app) # Initializes auth components, using app.config

    # --- In-memory User Store & Repositories ---
    app.users = {} # Stores User objects, keyed by username
    # Example: app.users['testuser@example.com'] = User(...)
    app.user_tokens = {} # For refresh tokens if implemented: keyed by user_id, stores refresh_token

    app.farm_repo = FarmRepository()
    app.flock_repo = FlockRepository()
    app.tracking_repo = TrackingRepository()

    # --- Helper to create standardized error responses ---
    def make_error_response(message, error_code, status_code, details=None):
        response_data = {"message": message, "error_code": error_code}
        if details:
            response_data["details"] = details
        return jsonify(response_data), status_code

    # --- Health Check ---
    @app.route('/api/health', methods=['GET'])
    @token_required
    def health_check():
        return jsonify({"status": "healthy", "message": "Farm Management API is running!"}), 200

    # --- Authentication Routes ---
    @app.route('/api/auth/register', methods=['POST'])
    def register():
        data = request.get_json()
        if not data:
            return make_error_response("Request body must be JSON.", "BAD_REQUEST", 400)

        username = data.get('username')
        password = data.get('password')
        full_name = data.get('full_name')

        validation_errors = {}
        if not username: validation_errors['username'] = ["Username is required."]
        if not password: validation_errors['password'] = ["Password is required."]
        # Add more password strength rules if needed
        if len(password or "") < 8 : validation_errors.setdefault('password', []).append("Password must be at least 8 characters.")

        if validation_errors:
            return make_error_response("Input validation failed.", "VALIDATION_ERROR", 400, validation_errors)

        if username in app.users:
            return make_error_response("Username already exists.", "USERNAME_ALREADY_EXISTS", 400, {"username": ["Username already exists."]})

        user_id = str(uuid.uuid4())
        hashed_password = generate_password_hash(password)
        new_user = User(user_id=user_id, username=username, password_hash=hashed_password, full_name=full_name)
        app.users[username] = new_user

        current_app.logger.info(f"User registered: {username}")
        return jsonify(new_user.to_dict()), 201

    @app.route('/api/auth/login', methods=['POST'])
    def login():
        data = request.get_json()
        if not data:
            return make_error_response("Request body must be JSON.", "BAD_REQUEST", 400)

        username = data.get('username')
        password = data.get('password')

        if not username or not password:
            return make_error_response("Username and password are required.", "MISSING_CREDENTIALS", 400)

        user = app.users.get(username)
        if user and check_password_hash(user.password_hash, password):
            access_token = generate_jwt(user_id=user.user_id)
            # refresh_token = generate_refresh_jwt(user.user_id) # If implementing refresh tokens
            # app.user_tokens[user.user_id] = refresh_token

            current_app.logger.info(f"User logged in: {username}")
            return jsonify({
                "access_token": access_token,
                # "refresh_token": refresh_token, # If implementing
                "token_type": "bearer",
                "expires_in": current_app.config['JWT_EXPIRATION_DELTA'].total_seconds()
            }), 200
        else:
            current_app.logger.warning(f"Failed login attempt for username: {username}")
            return make_error_response("Invalid username or password.", "INVALID_CREDENTIALS", 401)

    @app.route('/api/auth/me', methods=['GET'])
    @token_required
    def get_current_user():
        user_id = g.current_user_id # Set by @token_required
        # Find user by user_id (requires iterating app.users or a different lookup structure if not by username)
        found_user = None
        for user_obj in app.users.values():
            if user_obj.user_id == user_id:
                found_user = user_obj
                break

        if found_user:
            return jsonify(found_user.to_dict()), 200
        else:
            # This case should ideally not happen if token is valid and user_id exists
            current_app.logger.error(f"User with ID {user_id} from token not found in user store.")
            return make_error_response("User not found.", "USER_NOT_FOUND", 404)

    # TODO: Implement /api/auth/refresh if needed

    # --- Register Blueprints for other routes ---
    register_farm_routes(app)
    register_flock_routes(app)
    register_tracking_routes(app)
    register_environment_routes(app)
    register_weather_routes(app)

    # --- Centralized Error Handling (Updated to use make_error_response) ---
    @app.errorhandler(400)
    def handle_bad_request(error):
        msg = error.description if hasattr(error, 'description') and error.description else "The request was malformed or invalid."
        details = error.data.get("messages") if hasattr(error, 'data') and isinstance(error.data, dict) and "messages" in error.data else None
        return make_error_response(msg, "BAD_REQUEST", 400, details)

    @app.errorhandler(401)
    def handle_unauthorized(error):
        msg = error.description if hasattr(error, 'description') and error.description else "Authentication is required and has failed or has not yet been provided."
        return make_error_response(msg, "UNAUTHORIZED", 401)

    @app.errorhandler(403)
    def handle_forbidden(error):
        msg = error.description if hasattr(error, 'description') and error.description else "You do not have permission to access this resource."
        return make_error_response(msg, "FORBIDDEN", 403)

    @app.errorhandler(404)
    def handle_not_found(error):
        msg = error.description if hasattr(error, 'description') and error.description else "The requested resource was not found on the server."
        return make_error_response(msg, "NOT_FOUND", 404)

    @app.errorhandler(405)
    def handle_method_not_allowed(error):
        msg = error.description if hasattr(error, 'description') and error.description else "The method is not allowed for the requested URL."
        return make_error_response(msg, "METHOD_NOT_ALLOWED", 405)

    @app.errorhandler(409)
    def handle_conflict(error):
        msg = error.description if hasattr(error, 'description') and error.description else "A conflict occurred with the current state of the resource."
        return make_error_response(msg, "CONFLICT", 409)

    @app.errorhandler(500)
    def handle_internal_server_error(error):
        app.logger.error(f"Internal Server Error: {error}", exc_info=True)
        return make_error_response("An unexpected error occurred on the server. Please try again later.", "INTERNAL_SERVER_ERROR", 500)

    @app.errorhandler(Exception)
    def handle_unhandled_exception(error):
        app.logger.error(f"Unhandled Exception: {error}", exc_info=True)
        return make_error_response("An unexpected application error occurred.", "UNHANDLED_EXCEPTION", 500)

    return app

if __name__ == '__main__':
    # This allows running the Flask development server directly
    # python -m farm_management.api.server
    app = create_app()
    # Host 0.0.0.0 makes it accessible externally (e.g., from Android emulator/device on same network)
    # Debug=True is useful for development, auto-reloads on code changes.
    app.run(host='0.0.0.0', port=5000, debug=True)
