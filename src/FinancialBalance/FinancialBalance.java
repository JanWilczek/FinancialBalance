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
 * The main logic-handling class providing FinancialBalance interface.
 *
 */
public class FinancialBalance {
	private List<Expense> expenses;
	private Map<YearMonth, MonthlyReport> monthlyReports;
	private DataProvider dataProvider;
	private String name;

	
	private boolean databaseUpdateScheduled = false; ///< a flag informing whether database needs an update 
	
	FinancialBalance(String name)
	{
		this.name = name;
		
		dataProvider = new TextfileDataProvider(this.name + ".txt");
		expenses = dataProvider.getExpenses();
		
		Collections.sort(expenses);
		
		monthlyReports = new TreeMap<>();
		generateMonthlyReports();
	}
	
	// Public members functions
	
	/**
	 * Adds an expense to the internal container.
	 * A call to this function does not add an according record in the database. In order to do that updateDatbase() needs to be called after, what is marked by databaseUpdateScheduled flag.
	 * @param expenseToAdd
	 * @return index at which the expense was added
	 */
	public int addExpense(Expense expenseToAdd)
	{
		expenses.add(expenseToAdd);
		Collections.sort(expenses);		// TODO: [RESEARCH] Is there a better way to do it than sorting all elements? Is sorting them costly?
		generateMonthlyReports();
		databaseUpdateScheduled = true;
		return expenses.indexOf(expenseToAdd);
	}
	
	/**
	 * Deletes the given expense from the internal container.
	 * A call to this function does not delete the according record in the database. In order to do that updateDatbase() needs to be called after, what is marked by databaseUpdateScheduled flag.
	 * @param expenseToDelete
	 * @return boolean value stating if the expense has been found and deleted
	 */
	public boolean deleteExpense(Expense expenseToDelete)
	{
		for (Expense expense : expenses)
			if (expense.equals(expenseToDelete))
			{
				expenses.remove(expense);
				generateMonthlyReports();
				databaseUpdateScheduled = true;
				return true;
			}
		return false;
	}
	
	/**
	 * Deletes expense at the given index from the internal container.
	 * A call to this function does not delete the according record in the database. In order to do that updateDatbase() needs to be called after, what is marked by databaseUpdateScheduled flag.
	 * @param expenseToDeleteIndex
	 * @return boolean value stating if the expense has been found and deleted
	 */
	public boolean deleteExpense(int expenseToDeleteIndex)
	{
		if (expenseToDeleteIndex > 0 && expenseToDeleteIndex < expenses.size())
		{
				expenses.remove(expenseToDeleteIndex);
				generateMonthlyReports();
				databaseUpdateScheduled = true;
				return true;
		}
		return false;
	}
	
	/**
	 * Updates the database file objects when databaseUpdateScheduled flag is true.
	 */
	public void updateDatabase()
	{
		if (databaseUpdateScheduled) {
			dataProvider.updateDatabase(expenses);
		}
	}
	
	/**
	 * @return expenses
	 *  the internal container of expenses
	 */
	public List<Expense> getExpenses()
	{
		return expenses;
	}
	
	/**
	 * @return monthly reports
	 * The expenses monthly reports generated from the database.
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
}
