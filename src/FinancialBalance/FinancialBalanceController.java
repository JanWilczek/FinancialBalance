package FinancialBalance;

/**
 * 
 * @author Jan F. Wilczek
 * @date 14.01.18
 * @version 1.0
 * A class serving as a bridge between GUI (<code>FinancialBalanceView</code> class) and model (<code>Financial Balance</code> class).
 * 
 */
public class FinancialBalanceController {
	
	public FinancialBalanceController(FinancialBalance financialBalance, FinancialBalanceView financialBalanceView)
	{
		this.financialBalance = financialBalance;
		this.financialBalanceView = financialBalanceView;
	}
	
	private FinancialBalance financialBalance;
	private FinancialBalanceView financialBalanceView;
}
