#!/usr/bin/env python3
"""
Farm Management API Server Startup Script
Starts the Flask server with all endpoints for Android integration
"""

import os
import sys
from api.server import create_app

def main():
    """Start the Farm Management API server"""
    
    print("=" * 60)
    print("🐓 ROOSTER FARM MANAGEMENT API SERVER")
    print("=" * 60)
    print("Phase 4: Cross-Platform Integration")
    print("Python Backend ↔️ Android Frontend")
    print("-" * 60)
    
    # Create Flask app
    app = create_app()
    
    print("📡 Server Configuration:")
    print(f"   • Host: 0.0.0.0 (accessible from Android)")
    print(f"   • Port: 5000")
    print(f"   • Android Emulator URL: http://10.0.2.2:5000")
    print(f"   • Physical Device URL: http://[YOUR_IP]:5000")
    print()
    
    print("🛜 Available Endpoints:")
    print("   FARM MANAGEMENT:")
    print("   • GET  /api/health                    - Health check")
    print("   • GET  /api/farms                     - List all farms")
    print("   • GET  /api/farms/{id}                - Get farm details")
    print("   • POST /api/farms                     - Create farm")
    print("   • PUT  /api/farms/{id}                - Update farm")
    print("   • DELETE /api/farms/{id}              - Delete farm")
    print()
    print("   DASHBOARD DATA:")
    print("   • GET  /farm/details/{id}             - Farm basic info")
    print("   • GET  /farm/production_summary/{id}  - Production metrics")
    print("   • GET  /farm/health_alerts/{id}       - Health alerts")
    print("   • POST /farm/health_alerts/{id}/{alert_id}/read")
    print()
    print("   WEATHER:")
    print("   • GET  /weather/current_by_coords     - Weather by coordinates")
    print("   • GET  /weather/current_by_location   - Weather by location")
    print()
    
    print("🔑 Authentication:")
    print("   • Bearer Token required for most endpoints")
    print("   • Use Authorization: Bearer <token> header")
    print()
    
    print("📱 Android Integration:")
    print("   • API models created in core-network module")
    print("   • Retrofit services configured")
    print("   • Repository implementations ready")
    print("   • FarmerHomeScreen integrated")
    print()
    
    print("🚀 Starting server...")
    print("   Press Ctrl+C to stop")
    print("=" * 60)
    
    try:
        # Run the Flask development server
        app.run(
            host='0.0.0.0',
            port=5000,
            debug=True,
            use_reloader=True
        )
    except KeyboardInterrupt:
        print("\n" + "=" * 60)
        print("🛑 Server stopped by user")
        print("=" * 60)
    except Exception as e:
        print(f"\n❌ Server error: {e}")
        sys.exit(1)

if __name__ == '__main__':
    main()