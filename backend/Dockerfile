# Use Node.js 18 Alpine for smaller image size
FROM node:18-alpine

# Set working directory
WORKDIR /app

# Install system dependencies for better performance
RUN apk add --no-cache \
    tini \
    curl \
    && rm -rf /var/cache/apk/*

# Copy package files first for better caching
COPY package*.json ./

# Install dependencies with production optimizations
RUN npm ci --only=production --silent && \
    npm cache clean --force

# Create non-root user for security
RUN addgroup -g 1001 -S nodejs && \
    adduser -S rooster -u 1001

# Copy application code
COPY --chown=rooster:nodejs . .

# Remove unnecessary files to reduce image size
RUN rm -rf \
    .env.example \
    README.md \
    Dockerfile \
    .git* \
    *.md

# Switch to non-root user
USER rooster

# Expose port
EXPOSE 3000

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:3000/health || exit 1

# Use tini for proper signal handling
ENTRYPOINT ["/sbin/tini", "--"]

# Start the application
CMD ["node", "server.js"]

# Add labels for better maintainability
LABEL name="rooster-price-prediction-api" \
      version="1.0.0" \
      description="Price prediction API for poultry optimized for rural farmers" \
      maintainer="Rooster Project Team"