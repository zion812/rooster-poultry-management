import datetime
import random

class FinancialReportsScreen:
    def __init__(self):
        # These would be aggregates from other systems (accounting, sales, expenses)
        self.financial_data = self._generate_mock_financial_data()

    def _generate_mock_financial_data(self, year=datetime.date.today().year, month=datetime.date.today().month):
        data = {"year": year, "month": month}

        # --- Income Statement (Profit & Loss) Data ---
        data["revenue"] = {
            "consultation_fees": round(random.uniform(50000, 150000), 2),
            "marketplace_commissions": round(random.uniform(30000, 100000), 2),
            "subscription_fees": round(random.uniform(10000, 50000), 2),
            "other_income": round(random.uniform(1000, 5000), 2),
        }
        data["total_revenue"] = sum(data["revenue"].values())

        data["cost_of_goods_sold"] = { # COGS, if applicable (e.g. for physical products if sold directly)
            "platform_service_costs": round(data["total_revenue"] * random.uniform(0.05, 0.10), 2), # Costs directly tied to delivering services
        }
        data["total_cogs"] = sum(data["cost_of_goods_sold"].values())
        data["gross_profit"] = data["total_revenue"] - data["total_cogs"]

        data["operating_expenses"] = {
            "salaries_and_wages": round(random.uniform(20000, 60000), 2),
            "marketing_and_advertising": round(random.uniform(5000, 20000), 2),
            "software_and_tools": round(random.uniform(2000, 10000), 2),
            "rent_and_utilities": round(random.uniform(1000, 5000), 2),
            "payment_processing_fees": round(data["total_revenue"] * random.uniform(0.01, 0.03), 2),
            "customer_support": round(random.uniform(3000, 8000), 2),
            "other_operating_expenses": round(random.uniform(1000, 4000), 2),
        }
        data["total_operating_expenses"] = sum(data["operating_expenses"].values())
        data["operating_income"] = data["gross_profit"] - data["total_operating_expenses"] # EBIT

        data["non_operating"] = { # Interest, Taxes
            "interest_expense": round(data["operating_income"] * random.uniform(0, 0.02) if data["operating_income"] > 0 else 0, 2),
            "taxes": round(data["operating_income"] * random.uniform(0.1,0.2) if data["operating_income"] > 0 else 0, 2), # Simplified tax
        }
        data["net_income"] = data["operating_income"] - sum(data["non_operating"].values())


        # --- Balance Sheet Data (Simplified Summary) ---
        # Assets
        current_assets_cash = round(random.uniform(100000, 500000), 2)
        current_assets_receivables = round(data["total_revenue"] * random.uniform(0.1, 0.2), 2) # e.g. outstanding marketplace fees
        data["assets"] = {
            "current_assets": {
                "cash_and_equivalents": current_assets_cash,
                "accounts_receivable": current_assets_receivables,
                "prepaid_expenses": round(random.uniform(5000, 20000), 2),
            },
            "non_current_assets": {
                "property_plant_equipment_net": round(random.uniform(20000, 100000), 2),
                "intangible_assets_net": round(random.uniform(10000, 50000), 2), # e.g. software development costs capitalized
            }
        }
        data["total_current_assets"] = sum(data["assets"]["current_assets"].values())
        data["total_non_current_assets"] = sum(data["assets"]["non_current_assets"].values())
        data["total_assets"] = data["total_current_assets"] + data["total_non_current_assets"]

        # Liabilities
        current_liabilities_payable = round(data["total_operating_expenses"] * random.uniform(0.1, 0.15),2) # e.g. unpaid bills
        current_liabilities_deferred_revenue = round(data["revenue"]["subscription_fees"] * random.uniform(0.2,0.4),2) # unearned subscription revenue
        data["liabilities"] = {
            "current_liabilities": {
                "accounts_payable": current_liabilities_payable,
                "deferred_revenue": current_liabilities_deferred_revenue, # Subscriptions paid upfront
                "accrued_expenses": round(random.uniform(3000, 15000), 2),
            },
            "non_current_liabilities": {
                "long_term_debt": round(random.uniform(0, 50000), 2),
            }
        }
        data["total_current_liabilities"] = sum(data["liabilities"]["current_liabilities"].values())
        data["total_non_current_liabilities"] = sum(data["liabilities"]["non_current_liabilities"].values())
        data["total_liabilities"] = data["total_current_liabilities"] + data["total_non_current_liabilities"]

        # Equity (Derived: Assets - Liabilities)
        # In a real system, equity would have components like common stock, retained earnings
        data["total_equity"] = data["total_assets"] - data["total_liabilities"]
        data["equity"] = {
            "retained_earnings_placeholder": data["total_equity"] - 50000, # Assuming 50k is common stock
            "common_stock_placeholder": 50000,
        }

        return data

    def generate_profit_and_loss_statement(self, period_data):
        report = f"--- Profit and Loss Statement ---\n"
        report += f"For Period Ending: {period_data['month']}/{period_data['year']}\n\n" # Simplified, could be more specific (e.g. "Month of...")

        report += "Revenue:\n"
        for item, amount in period_data["revenue"].items():
            report += f"  {item.replace('_', ' ').title()}: ${amount:,.2f}\n"
        report += f"Total Revenue: ${period_data['total_revenue']:,.2f}\n\n"

        report += "Cost of Goods Sold:\n"
        for item, amount in period_data["cost_of_goods_sold"].items():
            report += f"  {item.replace('_', ' ').title()}: ${amount:,.2f}\n"
        report += f"Total COGS: ${period_data['total_cogs']:,.2f}\n"
        report += f"Gross Profit: ${period_data['gross_profit']:,.2f}\n\n"

        report += "Operating Expenses:\n"
        for item, amount in period_data["operating_expenses"].items():
            report += f"  {item.replace('_', ' ').title()}: ${amount:,.2f}\n"
        report += f"Total Operating Expenses: ${period_data['total_operating_expenses']:,.2f}\n"
        report += f"Operating Income (EBIT): ${period_data['operating_income']:,.2f}\n\n"

        report += "Non-Operating Income/Expenses:\n"
        report += f"  Interest Expense: ${period_data['non_operating']['interest_expense']:,.2f}\n"
        report += f"Taxes: ${period_data['non_operating']['taxes']:,.2f}\n"
        report += f"Net Income: ${period_data['net_income']:,.2f}\n"

        return report

    def generate_balance_sheet_summary(self, period_data):
        report = f"--- Balance Sheet Summary ---\n"
        report += f"As of: {period_data['month']}/{period_data['year']}\n\n" # Simplified

        report += "Assets:\n"
        report += "  Current Assets:\n"
        for item, amount in period_data["assets"]["current_assets"].items():
            report += f"    {item.replace('_', ' ').title()}: ${amount:,.2f}\n"
        report += f"  Total Current Assets: ${period_data['total_current_assets']:,.2f}\n"
        report += "  Non-Current Assets:\n"
        for item, amount in period_data["assets"]["non_current_assets"].items():
            report += f"    {item.replace('_', ' ').title()}: ${amount:,.2f}\n"
        report += f"  Total Non-Current Assets: ${period_data['total_non_current_assets']:,.2f}\n"
        report += f"Total Assets: ${period_data['total_assets']:,.2f}\n\n"

        report += "Liabilities:\n"
        report += "  Current Liabilities:\n"
        for item, amount in period_data["liabilities"]["current_liabilities"].items():
            report += f"    {item.replace('_', ' ').title()}: ${amount:,.2f}\n"
        report += f"  Total Current Liabilities: ${period_data['total_current_liabilities']:,.2f}\n"
        report += "  Non-Current Liabilities:\n"
        for item, amount in period_data["liabilities"]["non_current_liabilities"].items():
            report += f"    {item.replace('_', ' ').title()}: ${amount:,.2f}\n"
        report += f"  Total Non-Current Liabilities: ${period_data['total_non_current_liabilities']:,.2f}\n"
        report += f"Total Liabilities: ${period_data['total_liabilities']:,.2f}\n\n"

        report += "Equity:\n"
        # Simplified display for equity components
        for item, amount in period_data["equity"].items():
             report += f"  {item.replace('_', ' ').title()}: ${amount:,.2f}\n"
        report += f"Total Equity: ${period_data['total_equity']:,.2f}\n\n"

        report += f"Total Liabilities and Equity: ${period_data['total_liabilities'] + period_data['total_equity']:,.2f}\n"
        # Sanity check: Total Assets should equal Total Liabilities + Total Equity
        if abs((period_data['total_liabilities'] + period_data['total_equity']) - period_data['total_assets']) > 0.01: # Check for floating point issues
            report += "Error: Balance sheet does not balance!\n"

        return report

    def display_screen(self):
        print("---- Financial Reports Screen ----")

        # For current period (e.g., current month/year based on mock data generation)
        current_period_data = self.financial_data

        print("\nGenerating Profit and Loss Statement for current period...")
        pnl_report = self.generate_profit_and_loss_statement(current_period_data)
        print(pnl_report)

        print("\nGenerating Balance Sheet Summary for current period...")
        bs_summary_report = self.generate_balance_sheet_summary(current_period_data)
        print(bs_summary_report)

        # Example: Generating for a previous period (would require historical data storage in real app)
        # print("\nGenerating report for a previous period (mocked by regenerating data)...")
        # previous_period_data = self._generate_mock_financial_data(year=self.financial_data["year"], month=self.financial_data["month"]-1 if self.financial_data["month"] > 1 else 12)
        # pnl_report_prev = self.generate_profit_and_loss_statement(previous_period_data)
        # print(pnl_report_prev)

        print("---------------------------------")

if __name__ == '__main__':
    screen = FinancialReportsScreen()
    screen.display_screen()
