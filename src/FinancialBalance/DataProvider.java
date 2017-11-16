package FinancialBalance;

import java.util.List;

/**
 * 
 * @author Jan F. Wilczek
 * @date 16.11.2017
 * 
 * @version 1.0
 * 
 * A general database interaction interface.
 * 
 */
public interface DataProvider {
	
	/**
	 * @return all expenses from database.
	 */
	public List<Expense> getExpenses();
	
	/**
	 *  Updates the database with current FinancialBalance state.
	 */
	public void updateDatabase(List<Expense> expenses);
	
	/**
	 * Clears the internal database. Use with care.
	 */
	public void clearDatabase();
}
