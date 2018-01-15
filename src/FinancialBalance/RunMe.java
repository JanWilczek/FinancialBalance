/**
 * public class RunMe
 * package FinancialBalance
 * @author Jan Wilczek
 * Main application class intended to be launched
 */


package FinancialBalance;

/**
 * The main thread class.
 * @author Jan Wilczek
 *
 */
public class RunMe {
	public static void main(String [] args){
		FinancialBalance financialBalance = new FinancialBalance((DataProvider) new SQLiteDatabaseDataProvider("expenses.db"));
		FinancialBalanceView financialBalanceView = new FinancialBalanceView();
		@SuppressWarnings("unused")
		FinancialBalanceController financialBalanceController = new FinancialBalanceController(financialBalance, financialBalanceView);
	}
}