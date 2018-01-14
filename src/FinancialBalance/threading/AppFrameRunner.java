package FinancialBalance.threading;

import FinancialBalance.FinancialBalanceView;
import FinancialBalance.FinancialBalance;

/**
 * 
 * @author Jan F. Wilczek
 * @date 19.11.17
 * @version 1.0
 * 
 *	A threading utility used for displaying the main application window in a separate thread.
 */
public class AppFrameRunner implements Runnable
{
	private FinancialBalance financialBalance;
	
	public AppFrameRunner(FinancialBalance financialBalance)
	{
		this.financialBalance = financialBalance;
	}		
	
	@Override
	public void run() {
		@SuppressWarnings("unused")
		FinancialBalanceView frame = new FinancialBalanceView(financialBalance);
	}
}
