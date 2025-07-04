 feature/python-rest-api-wrapper
from flask import Flask, jsonify, g
from flask_cors import CORS

# Local application imports
from .auth import token_required, init_app as init_auth_app
from .farm_routes import register_farm_routes
from .flock_routes import register_flock_routes
from .tracking_routes import register_tracking_routes

from flask import Flask, jsonify, g # Added g for request context
from flask_cors import CORS
from .auth import token_required, init_app as init_auth_app # Import auth components

# Import repository classes
 main
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
    init_auth_app(app)

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

    # Centralized error handling
    @app.errorhandler(400) # Bad Request
    def bad_request_error(error):
        response = jsonify({
            "error": "Bad Request",
            "message": error.description if hasattr(error, 'description') else "The request was malformed or invalid."
        })
        response.status_code = 400
        return response

    @app.errorhandler(401) # Unauthorized
    def unauthorized_error(error):
        response = jsonify({
            "error": "Unauthorized",
            "message": error.description if hasattr(error, 'description') else "Authentication is required and has failed or has not yet been provided."
        })
        response.status_code = 401
        return response

    @app.errorhandler(404) # Not Found
    def not_found_error(error):
        response = jsonify({
            "error": "Not Found",
            "message": error.description if hasattr(error, 'description') else "The requested resource was not found on the server."
        })
        response.status_code = 404
        return response

    @app.errorhandler(405) # Method Not Allowed
    def method_not_allowed_error(error):
        response = jsonify({
            "error": "Method Not Allowed",
            "message": error.description if hasattr(error, 'description') else "The method is not allowed for the requested URL."
        })
        response.status_code = 405
        return response

    @app.errorhandler(409) # Conflict
    def conflict_error(error):
        response = jsonify({
            "error": "Conflict",
            "message": error.description if hasattr(error, 'description') else "A conflict occurred with the current state of the resource."
        })
        response.status_code = 409
        return response

    @app.errorhandler(500) # Internal Server Error
    def internal_server_error(error):
        # Log the error for server-side debugging
        app.logger.error(f"Internal Server Error: {error}", exc_info=True)
        response = jsonify({
            "error": "Internal Server Error",
            "message": "An unexpected error occurred on the server. Please try again later."
        })
        response.status_code = 500
        return response

    @app.errorhandler(Exception) # Catch-all for other exceptions
    def unhandled_exception(error):
        # Log the error for server-side debugging
        app.logger.error(f"Unhandled Exception: {error}", exc_info=True)
        response = jsonify({
            "error": "Internal Server Error", # Generic error message for unexpected issues
            "message": "An unexpected application error occurred."
        })
        response.status_code = 500 # Or a more specific code if identifiable
        return response

    return app

if __name__ == '__main__':
    # This allows running the Flask development server directly
    # python -m farm_management.api.server
    app = create_app()
    # Host 0.0.0.0 makes it accessible externally (e.g., from Android emulator/device on same network)
    # Debug=True is useful for development, auto-reloads on code changes.
    app.run(host='0.0.0.0', port=5000, debug=True)
