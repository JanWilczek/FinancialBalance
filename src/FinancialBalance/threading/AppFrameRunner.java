package FinancialBalance.threading;

import FinancialBalance.AppFrame;
import FinancialBalance.FinancialBalance;

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
		AppFrame frame = new AppFrame(financialBalance);
	}
}
