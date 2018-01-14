/**
 * public class RunMe
 * package FinancialBalance
 * @author Jan Wilczek
 * Main application class intended to be launched
 */


package FinancialBalance;

import FinancialBalance.threading.*;
import javax.swing.SwingUtilities;

/**
 * The main thread class.
 * @author Jan Wilczek
 *
 */
public class RunMe {
	public static void main(String [] args){
		FinancialBalance financialBalance = new FinancialBalance("expenses");
		FinancialBalanceView financialBalanceView = new FinancialBalanceView();
		@SuppressWarnings("unused")
		FinancialBalanceController financialBalanceController = new FinancialBalanceController(financialBalance, financialBalanceView);
		//SwingUtilities.invokeLater(new AppFrameRunner(fb));
	}
}