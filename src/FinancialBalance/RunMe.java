/**
 * public class RunMe
 * package FinancialBalance
 * @author Jan Wilczek
 * Main application class intended to be launched
 */


package FinancialBalance;


public class RunMe {
	public static void main(String [] args){
		FinancialBalance fb = new FinancialBalance();
		AppFrame frame = new AppFrame(fb);
	}
}
