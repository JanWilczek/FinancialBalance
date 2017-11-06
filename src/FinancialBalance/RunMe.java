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
		FinancialBalance fb = new FinancialBalance();
		@SuppressWarnings("unused")
		AppFrame frame = new AppFrame(fb);
	}
}
