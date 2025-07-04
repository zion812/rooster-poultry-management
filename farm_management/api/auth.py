from functools import wraps
from flask import request, jsonify, current_app

# This is a very simple, hardcoded token for demonstration purposes.
# In a real application, this should be securely managed and not hardcoded.
# Consider environment variables or a configuration file for this.
STATIC_API_TOKEN = "DEV_TOKEN_12345_STATIC" # Replace with a more secure token if deploying

def token_required(f):
    """
    Decorator to ensure that a valid API token is present in the request header.
    """
    @wraps(f)
    def decorated_function(*args, **kwargs):
        token = None
        if 'Authorization' in request.headers:
            auth_header = request.headers['Authorization']
            try:
                # Expecting "Bearer <token>"
                token_type, token_value = auth_header.split()
                if token_type.lower() == 'bearer':
                    token = token_value
            except ValueError:
                # Handle cases where the header is malformed (e.g., not two parts)
                pass # Token remains None

        if not token:
            return jsonify({"message": "Token is missing or header is malformed!", "error": "Unauthorized"}), 401

        # In a real app, you might look up the token in a database or validate it more robustly.
        # Here, we just compare with our static token.
        if token == STATIC_API_TOKEN:
            # You could also attach user information to `g` or `current_user` here if needed
            # g.current_user = get_user_from_token(token) # Example
            return f(*args, **kwargs)
        else:
            return jsonify({"message": "Token is invalid or expired!", "error": "Unauthorized"}), 401

    return decorated_function

def init_app(app):
    """
    Can be used to initialize auth settings or configurations if needed,
    though for this simple static token, it's not strictly necessary.
    It's good practice for more complex auth setups.
    """
    # Example: app.config.setdefault('API_TOKEN', 'some_default_token_from_config')
    # current_app.config['STATIC_API_TOKEN'] = STATIC_API_TOKEN # Making it accessible via app.config
    pass
