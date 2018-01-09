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
	public boolean addExpense(Expense expenseToAdd);
	
	/**
	 * Deletes the given expense.
	 * @param expenseToDelete
	 * @return true if the expense has been deleted
	 */
	public boolean deleteExpense(Expense expenseToDelete);
	
	//	TODO: To implement.
	//public boolean updateExpense(Expense expenseToUpdate, Expense expenseUpdated);
	
	/**
	 * Clears the internal database. Use with care.
	 */
	public void clearDatabase();
	
	/**
	 * Performs closing action.
	 */
	public void close();
	
	/**
	 * Overrides the existing database with the given expenses.
	 * @param expenses
	 */
	public void updateDatabase(List<Expense> expenses);
	
	/**
	 * @return name of the file database is stored in
	 */
	public String getDatabaseFileName();
}
