import datetime

class SystemMonitoringScreen:
    def __init__(self):
        self.server_health = {}
        self.api_performance = {}
        self.error_tracking = []

    def get_server_health(self):
        # Mock data for server health
        self.server_health = {
            "server1": {"status": "online", "cpu_usage": "15%", "memory_usage": "45GB/64GB"},
            "server2": {"status": "online", "cpu_usage": "25%", "memory_usage": "50GB/64GB"},
            "server3": {"status": "offline", "cpu_usage": "N/A", "memory_usage": "N/A"},
        }
        return self.server_health

    def get_api_performance(self):
        # Mock data for API performance
        self.api_performance = {
            "/api/users": {"avg_response_time": "120ms", "error_rate": "0.5%", "requests_per_minute": 1200},
            "/api/pets": {"avg_response_time": "150ms", "error_rate": "1.2%", "requests_per_minute": 800},
            "/api/consultations": {"avg_response_time": "200ms", "error_rate": "0.2%", "requests_per_minute": 500},
        }
        return self.api_performance

    def get_error_tracking(self):
        # Mock data for error tracking
        self.error_tracking = [
            {"timestamp": datetime.datetime.now() - datetime.timedelta(minutes=5), "error_code": 500, "message": "Internal Server Error on /api/payments", "source": "server2"},
            {"timestamp": datetime.datetime.now() - datetime.timedelta(minutes=15), "error_code": 404, "message": "Resource not found on /api/users/unknown", "source": "server1"},
            {"timestamp": datetime.datetime.now() - datetime.timedelta(minutes=30), "error_code": 403, "message": "Forbidden access to /api/admin/config", "source": "server1"},
        ]
        return self.error_tracking

    def display_screen(self):
        print("---- System Monitoring Screen ----")
        print("\n-- Server Health --")
        for server, data in self.get_server_health().items():
            print(f"  {server}: Status: {data['status']}, CPU: {data['cpu_usage']}, Memory: {data['memory_usage']}")

        print("\n-- API Performance --")
        for endpoint, data in self.get_api_performance().items():
            print(f"  {endpoint}: Avg Response: {data['avg_response_time']}, Error Rate: {data['error_rate']}, RPM: {data['requests_per_minute']}")

        print("\n-- Error Tracking --")
        for error in self.get_error_tracking():
            print(f"  Timestamp: {error['timestamp']}, Code: {error['error_code']}, Message: {error['message']}, Source: {error['source']}")
        print("---------------------------------")

if __name__ == '__main__':
    screen = SystemMonitoringScreen()
    screen.display_screen()
