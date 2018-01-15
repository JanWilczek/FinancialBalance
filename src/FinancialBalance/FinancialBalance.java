/**
 * FinancialBalance.java
 * @author Jan Wilczek
 * University of Science in Cracow
 * 1.05.2017
 *
 *Program to handle monthly income and outcome.
 *Enables to add and delete outcomes sorted by category and date.
 *Prints a financial statement at the end of each month.
 *Send an e-mail with monthly balance at the end of the month.
 *Target platform: Android
 *
 *This class serves as the main application class
 */

package FinancialBalance;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Set;
import java.util.Calendar;
import java.util.Collections;
import java.time.YearMonth;

/**
 * 
 * @author Jan Wilczek
 * @date 10.09.2017
 * 
 * @version 1.0
 * 
 * The main logic-handling class providing application's interface.
 *
 */
public class FinancialBalance {
	private List<Expense> expenses;
	private Map<YearMonth, MonthlyReport> monthlyReports;
	private DataProvider dataProvider;
	
	/**
	 * Public constructor. Requires a name for the database.
	 */
	public FinancialBalance(DataProvider dataProvider)
	{
		this.dataProvider = dataProvider;
		expenses = this.dataProvider.getExpenses();
		
		Collections.sort(expenses);
		
		monthlyReports = new TreeMap<>();
		generateMonthlyReports();
	}
	
	// Public members functions
	
	/**
	 * Adds an expense to the database.
	 * @param expenseToAdd
	 * @return index at which the expense was added
	 */
	public int addExpense(Expense expenseToAdd)
	{
		if (dataProvider.addExpense(expenseToAdd)) {
			addExpenseToSortedList(expenseToAdd);
						
			// Updating monthly reports
			YearMonth key = YearMonth.of(expenseToAdd.getDate().get(Calendar.YEAR), expenseToAdd.getDate().get(Calendar.MONTH) + 1);
			MonthlyReport report = monthlyReports.get(key);
			if (report == null)
			{
				report = new MonthlyReport(key, null);	// there are no expenses for this report, except for the new one.
				monthlyReports.put(key, report);
			}
			report.onExpenseAdded(expenseToAdd);
			
			return expenses.indexOf(expenseToAdd);
		}
		return -1;
	}
	
	/**
	 * Adds a range of expenses to the database.
	 */
	//TODO: To implement.
	//public int addExpenses(List<Expense> expensesToAdd)
	
	/**
	 * Deletes the given expense from the database.
	 * @param expenseToDelete
	 * @return boolean value stating if the expense has been found and deleted
	 */
	public boolean deleteExpense(Expense expenseToDelete)
	{
		if (dataProvider.deleteExpense(expenseToDelete))
		{
			expenses.remove(expenseToDelete);
			monthlyReports.get(YearMonth.of(expenseToDelete.getDate().get(Calendar.YEAR), expenseToDelete.getDate().get(Calendar.MONTH) + 1)).onExpenseDeleted(expenseToDelete);
			return true;			
		}
		return false;
	}
	
	/**
	 * Deletes expense at the given index from the internal container.
	 * @param expenseToDeleteIndex
	 * @return boolean value stating if the expense has been found and deleted
	 */
	public boolean deleteExpense(int expenseToDeleteIndex)
	{
		Expense expenseToDelete = expenses.get(expenseToDeleteIndex);
		if (dataProvider.deleteExpense(expenseToDelete))
		{
			expenses.remove(expenseToDelete);
			monthlyReports.get(YearMonth.of(expenseToDelete.getDate().get(Calendar.YEAR), expenseToDelete.getDate().get(Calendar.MONTH) + 1)).onExpenseDeleted(expenseToDelete);
			return true;
		}
		return false;
	}
	
	
	/**
	 * @return the internal container of expenses
	 */
	public List<Expense> getExpenses()
	{
		return expenses;
	}
	
	/**
	 * @return monthly reports generated from the database.
	 */
	public Map<YearMonth, MonthlyReport> getMonthlyReports()
	{
		return monthlyReports;
	}
	
	/**
	 * Clears the internal database. Use with care.
	 */
	public void clearDatabase()
	{
		expenses.clear();
		dataProvider.clearDatabase();
	}
	
	/**
	 * Perform all necessary closing actions.
	 * Should be invoked before application's termination, otherwise data may be lost.
	 */
	public void close()
	{
		dataProvider.close();
	}
	
	/**
	 * @return inner data provider
	 */
	public DataProvider getDataProvider() 
	{
		return dataProvider;
	}
	
	/**
	 * Generates monthly reports for all months that have appeared in the database.
	 */
	private void generateMonthlyReports()
	{
		if (expenses == null || monthlyReports == null) return;
		Set<YearMonth> monthSet = new TreeSet<YearMonth>();
		for (Expense expense : expenses)
		{
			monthSet.add(YearMonth.of(expense.getDate().get(Calendar.YEAR), (expense.getDate().get(Calendar.MONTH) + 1)));
		}
		for (YearMonth month : monthSet)
		{
			monthlyReports.put(month, new MonthlyReport(month, expenses));
		}
	}
	
	private void addExpenseToSortedList(Expense expenseToAdd) {
        if (expenses.size() == 0) {
        	expenses.add(expenseToAdd);
        } else if (expenses.get(0).compareTo(expenseToAdd) == 1) {
        	expenses.add(0, expenseToAdd);
        } else if (expenses.get(expenses.size() - 1).compareTo(expenseToAdd) == -1) {
        	expenses.add(expenses.size(), expenseToAdd);
        } else {
            int i = 0;
            while (expenses.get(i).compareTo(expenseToAdd) == -1) { i++; }
            expenses.add(i, expenseToAdd);
        }
    }

}
