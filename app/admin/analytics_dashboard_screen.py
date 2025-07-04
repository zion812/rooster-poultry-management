import datetime
import random

class AnalyticsDashboardScreen:
    def __init__(self):
        self.metrics = {}
        self._generate_mock_metrics()

    def _generate_mock_metrics(self):
        # Mock data for analytics
        today = datetime.date.today()
        self.metrics = {
            "key_performance_indicators": {
                "total_users": 15230,
                "active_users_monthly": 8500,
                "active_users_daily": 1200,
                "new_signups_last_30_days": 750,
                "user_retention_rate": "65%", # (e.g., month-over-month)
            },
            "user_behavior": {
                "average_session_duration": "15 minutes",
                "pages_per_session": 5.2,
                "most_visited_feature": "Veterinary Consultation",
                "feature_engagement_rate": { # % of active users using feature
                    "Consultations": "40%",
                    "Marketplace": "30%",
                    "Educational Content": "25%",
                    "Forum": "15%"
                },
                "user_demographics_summary": { # Simplified
                    "top_country": "USA (60%)",
                    "user_types": "Farmers (70%), Veterinarians (30%)"
                }
            },
            "revenue_tracking": {
                "total_revenue_mtd": 12500.75, # Month to Date
                "total_revenue_ytd": 150200.50, # Year to Date
                "average_revenue_per_user_arpu": 9.86, # YTD Total Revenue / Total Users
                "revenue_by_source": {
                    "Consultation Fees": 75000.00,
                    "Marketplace Commissions": 60200.50,
                    "Subscription Fees": 15000.00,
                },
                "recent_transactions_count_last_24h": 85,
            },
            "system_performance_summary": { # Could link to SystemMonitoringScreen
                "overall_uptime": "99.98%",
                "average_api_response_time": "140ms",
                "critical_errors_last_24h": 2,
            },
            "content_engagement":{
                "new_articles_published_last_30d": 25,
                "article_views_last_30d": 15000,
                "average_time_on_page_articles": "3 min 45 sec",
                "most_popular_article_id": "article_vet_health_basics"
            }
        }
        # Generate some time series data for a chart (mock)
        self.metrics["revenue_over_time_daily"] = []
        for i in range(30): # Last 30 days
            day = today - datetime.timedelta(days=i)
            daily_revenue = random.uniform(300, 800)
            self.metrics["revenue_over_time_daily"].append({"date": day.isoformat(), "revenue": round(daily_revenue,2)})
        self.metrics["revenue_over_time_daily"].reverse() # Chronological

        self.metrics["new_users_over_time_daily"] = []
        for i in range(30): # Last 30 days
            day = today - datetime.timedelta(days=i)
            new_users = random.randint(10, 50)
            self.metrics["new_users_over_time_daily"].append({"date": day.isoformat(), "users": new_users})
        self.metrics["new_users_over_time_daily"].reverse()


    def get_metrics(self, section=None):
        if section:
            return self.metrics.get(section)
        return self.metrics

    def display_screen(self):
        print("---- Analytics Dashboard Screen ----")

        kpis = self.get_metrics("key_performance_indicators")
        if kpis:
            print("\n-- Key Performance Indicators --")
            for key, value in kpis.items():
                print(f"  {key.replace('_', ' ').title()}: {value}")

        behavior = self.get_metrics("user_behavior")
        if behavior:
            print("\n-- User Behavior --")
            for key, value in behavior.items():
                if isinstance(value, dict):
                    print(f"  {key.replace('_', ' ').title()}:")
                    for sub_key, sub_value in value.items():
                        print(f"    {sub_key.replace('_', ' ').title()}: {sub_value}")
                else:
                    print(f"  {key.replace('_', ' ').title()}: {value}")

        revenue = self.get_metrics("revenue_tracking")
        if revenue:
            print("\n-- Revenue Tracking --")
            for key, value in revenue.items():
                if isinstance(value, dict):
                    print(f"  {key.replace('_', ' ').title()}:")
                    for sub_key, sub_value in value.items():
                        print(f"    {sub_key.replace('_', ' ').title()}: {sub_value:,.2f}" if isinstance(sub_value, float) else f"    {sub_key.replace('_', ' ').title()}: {sub_value}")
                else:
                     print(f"  {key.replace('_', ' ').title()}: {value:,.2f}" if isinstance(value, float) else f"  {key.replace('_', ' ').title()}: {value}")

        print("\n-- Revenue Over Last 30 Days (Sample) --")
        revenue_series = self.get_metrics("revenue_over_time_daily")
        if revenue_series:
            for item in revenue_series[:5]: # Display first 5 for brevity
                print(f"  Date: {item['date']}, Revenue: ${item['revenue']:.2f}")
            if len(revenue_series) > 5:
                print("  ... and more data points")

        print("\n-- New Users Over Last 30 Days (Sample) --")
        user_series = self.get_metrics("new_users_over_time_daily")
        if user_series:
            for item in user_series[:5]: # Display first 5 for brevity
                print(f"  Date: {item['date']}, New Users: {item['users']}")
            if len(user_series) > 5:
                print("  ... and more data points")

        print("-----------------------------------")

if __name__ == '__main__':
    screen = AnalyticsDashboardScreen()
    screen.display_screen()
