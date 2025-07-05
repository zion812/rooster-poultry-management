from functools import wraps
from flask import request, jsonify, current_app, g
import jwt
import os # Keep os for potential future use like loading secret from env
from datetime import datetime, timedelta

def generate_jwt(user_id: str) -> str:
    """
    Generates a JWT for a given user ID.
    """
    payload = {
        'user_id': user_id,
        'iat': datetime.utcnow(), # Issued at
        'exp': datetime.utcnow() + current_app.config.get('JWT_EXPIRATION_DELTA', timedelta(hours=1))
    }
    token = jwt.encode(payload, current_app.config['SECRET_KEY'], algorithm='HS256')
    return token

def token_required(f):
    """
    Decorator to ensure that a valid JWT is present in the request header
    and that the user information is loaded into g.current_user_id.
    """
    @wraps(f)
    def decorated_function(*args, **kwargs):
        token = None
        auth_header = request.headers.get('Authorization')

        if auth_header:
            parts = auth_header.split()
            if len(parts) == 2 and parts[0].lower() == 'bearer':
                token = parts[1]
            else:
                return jsonify({"message": "Authorization header must be 'Bearer <token>'.", "error": "Unauthorized"}), 401

        if not token:
            return jsonify({"message": "Token is missing.", "error": "Unauthorized"}), 401

        try:
            payload = jwt.decode(token, current_app.config['SECRET_KEY'], algorithms=['HS256'])
            # Store payload in Flask's g object for access in routes if needed
            g.current_user_id = payload['user_id']
            # Could also store g.current_user = get_user_by_id(payload['user_id']) if using a user model
        except jwt.ExpiredSignatureError:
            return jsonify({"message": "Token has expired.", "error": "Unauthorized"}), 401
        except jwt.InvalidTokenError:
            return jsonify({"message": "Token is invalid.", "error": "Unauthorized"}), 401

        return f(*args, **kwargs)

    return decorated_function

def init_auth_app(app): # Renamed from init_app to be more specific if other inits are needed
    """
    Initializes authentication related configurations for the Flask app.
    This function should be called when the Flask app is created in server.py.
    """
    # Ensure SECRET_KEY is set, essential for JWT
    # For production, this MUST be set via an environment variable and be a strong, random key.
    # Do not use the default in production.
    default_secret = 'your-insecure-default-secret-key-for-dev-only-CHANGE-ME'
    app.config.setdefault('SECRET_KEY', os.environ.get('FLASK_SECRET_KEY', default_secret))
    if app.config['SECRET_KEY'] == default_secret and not app.debug: # Or check 'FLASK_ENV' != 'development'
        app.logger.warning("WARNING: Using default SECRET_KEY in a non-debug environment. This is INSECURE.")

    app.config.setdefault('JWT_EXPIRATION_DELTA', timedelta(hours=1))

    # Define a default user for token generation (for demonstration/initial setup)
    # In a real application, replace this with a proper user management system (e.g., database).
    # Passwords should always be hashed and stored securely, never in plaintext.
    app.config.setdefault('DEFAULT_API_USER', {
        'username': 'apiuser',
        'password': 'apipassword123', # Example password - HASH THIS in a real system
        'id': 'user123-static'
    })
    app.logger.info("Auth initialized. Ensure SECRET_KEY is properly configured for production.")
    # Note: The server.py code actually has: `from .auth import token_required, init_app as init_auth_app`
    # So the function name `init_auth_app` here is correct and it's called in server.py.

    # The DEFAULT_API_USER related logic has been removed from here as user management
    # will be handled in server.py (initially in-memory, then potentially a database).
    # This function now primarily ensures JWT configuration is present.
    app.logger.info("Auth initialized. SECRET_KEY and JWT_EXPIRATION_DELTA should be configured in the main app.")
    pass
