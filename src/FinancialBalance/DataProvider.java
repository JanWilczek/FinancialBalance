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
	 * Adds and expense to the database.
	 * @param expenseToAdd
	 */
	public void addExpense(Expense expenseToAdd);
	
	/**
	 * Deletes the given expense.
	 * @param expenseToDelete
	 * @return true if the expense has been deleted
	 */
	public boolean deleteExpense(Expense expenseToDelete);
	
	/**
	 * Deletes expense at the given index
	 * @param indexExpenseToDelete
	 * @return true if the expense has been deleted
	 */
	public boolean deleteExpense(int indexExpenseToDelete);
	
	/**
	 *  Updates the database with current FinancialBalance state.
	 */
	public void updateDatabase(List<Expense> expenses);
	
	/**
	 * Clears the internal database. Use with care.
	 */
	public void clearDatabase();
	
	/**
	 * Performs closing action.
	 */
	public void onClose();
}
