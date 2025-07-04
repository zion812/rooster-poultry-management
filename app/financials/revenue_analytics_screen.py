import datetime
import random

class RevenueAnalyticsScreen:
    def __init__(self):
        self.revenue_data = {}
        self._generate_mock_revenue_data()

    def _generate_mock_revenue_data(self):
        # Mock data for revenue analytics
        today = datetime.date.today()

        # Revenue by source (monthly and YTD)
        sources = ["Consultation Fees", "Marketplace Commissions", "Subscription Fees", "Premium Content Sales"]
        revenue_by_source_mtd = {source: round(random.uniform(1000, 10000), 2) for source in sources}
        revenue_by_source_ytd = {source: val * random.uniform(8,12) for source, val in revenue_by_source_mtd.items()} # Approximate YTD

        total_revenue_mtd = sum(revenue_by_source_mtd.values())
        total_revenue_ytd = sum(revenue_by_source_ytd.values())

        # Revenue trends (e.g., daily for last 30 days, monthly for last 12 months)
        daily_revenue_trend = []
        for i in range(30):
            day = today - datetime.timedelta(days=i)
            daily_rev = sum(round(random.uniform(50, 200), 2) for _ in sources) # Simplified daily sum
            daily_revenue_trend.append({"date": day.isoformat(), "revenue": daily_rev})
        daily_revenue_trend.reverse()

        monthly_revenue_trend = []
        current_month = today.replace(day=1)
        for i in range(12):
            month_start = (current_month - datetime.timedelta(days=i*30)).replace(day=1) # Approximation
            monthly_rev = sum(round(random.uniform(5000, 20000), 2) for _ in sources) # Simplified monthly sum
            monthly_revenue_trend.append({"month": month_start.strftime("%Y-%m"), "revenue": monthly_rev})
        monthly_revenue_trend.reverse()

        # Customer Lifetime Value (CLV) - simplified
        clv_segments = {
            "farmers_standard": round(random.uniform(50, 150), 2),
            "farmers_premium": round(random.uniform(150, 400), 2),
            "veterinarians_basic": round(random.uniform(100, 250), 2),
            "veterinarians_pro": round(random.uniform(250, 600), 2),
            "overall_average": round(sum([random.uniform(50,600) for _ in range(4)])/4, 2)
        }

        # Churn Rate (mock)
        churn_rate_monthly = f"{random.uniform(1.5, 5.0):.2f}%"

        self.revenue_data = {
            "summary": {
                "total_revenue_mtd": total_revenue_mtd,
                "total_revenue_ytd": total_revenue_ytd,
                "average_revenue_per_paying_user_arppu_monthly": round(total_revenue_mtd / (1000 + random.randint(-100,100)),2), # Mock paying users
                "churn_rate_monthly": churn_rate_monthly,
            },
            "revenue_by_source": {
                "mtd": revenue_by_source_mtd,
                "ytd": revenue_by_source_ytd,
            },
            "revenue_trends": {
                "daily_last_30_days": daily_revenue_trend,
                "monthly_last_12_months": monthly_revenue_trend,
            },
            "customer_lifetime_value_clv": clv_segments,
            "top_revenue_generating_items": { # e.g. specific consultations, products
                "consult_specialist_A": {"type": "Consultation", "revenue_mtd": round(random.uniform(500,1500),2)},
                "product_feed_B": {"type": "Marketplace", "revenue_mtd": round(random.uniform(300,1000),2)},
                "subscription_vet_pro": {"type": "Subscription", "revenue_mtd": round(random.uniform(1000,2000),2)},
            }
        }

    def get_revenue_summary(self):
        return self.revenue_data.get("summary")

    def get_revenue_by_source(self, period="mtd"): # period can be 'mtd' or 'ytd'
        return self.revenue_data.get("revenue_by_source", {}).get(period)

    def get_revenue_trend(self, granularity="daily"): # granularity can be 'daily' or 'monthly'
        if granularity == "daily":
            return self.revenue_data.get("revenue_trends", {}).get("daily_last_30_days")
        elif granularity == "monthly":
            return self.revenue_data.get("revenue_trends", {}).get("monthly_last_12_months")
        return []

    def get_clv_data(self):
        return self.revenue_data.get("customer_lifetime_value_clv")

    def display_screen(self):
        print("---- Revenue Analytics Screen ----")

        summary = self.get_revenue_summary()
        if summary:
            print("\n-- Revenue Summary --")
            for key, value in summary.items():
                print(f"  {key.replace('_', ' ').title()}: ${value:,.2f}" if isinstance(value, float) else f"  {key.replace('_', ' ').title()}: {value}")

        print("\n-- Revenue by Source (MTD) --")
        source_mtd = self.get_revenue_by_source("mtd")
        if source_mtd:
            for source, amount in source_mtd.items():
                print(f"  {source}: ${amount:,.2f}")

        print("\n-- Revenue by Source (YTD) --")
        source_ytd = self.get_revenue_by_source("ytd")
        if source_ytd:
            for source, amount in source_ytd.items():
                print(f"  {source}: ${amount:,.2f}")

        print("\n-- Daily Revenue Trend (Last 5 Days) --")
        daily_trend = self.get_revenue_trend("daily")
        if daily_trend:
            for item in daily_trend[-5:]: # Last 5 days for brevity
                print(f"  Date: {item['date']}, Revenue: ${item['revenue']:,.2f}")

        print("\n-- Monthly Revenue Trend (Last 3 Months) --")
        monthly_trend = self.get_revenue_trend("monthly")
        if monthly_trend:
            for item in monthly_trend[-3:]: # Last 3 months for brevity
                print(f"  Month: {item['month']}, Revenue: ${item['revenue']:,.2f}")

        print("\n-- Customer Lifetime Value (CLV) by Segment --")
        clv_data = self.get_clv_data()
        if clv_data:
            for segment, value in clv_data.items():
                 print(f"  {segment.replace('_', ' ').title()}: ${value:,.2f}")

        print("\n-- Top Revenue Generating Items (MTD) --")
        top_items = self.revenue_data.get("top_revenue_generating_items", {})
        if top_items:
            for item, data in top_items.items():
                print(f"  Item: {item.replace('_', ' ').title()} ({data['type']}), Revenue: ${data['revenue_mtd']:,.2f}")

        print("---------------------------------")

if __name__ == '__main__':
    screen = RevenueAnalyticsScreen()
    screen.display_screen()
