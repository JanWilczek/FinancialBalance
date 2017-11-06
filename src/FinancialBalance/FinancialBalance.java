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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Set;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
	private String expensesFilePath = "expenses.txt";
	private Path expensesFile;
	private final static Charset charset = Charset.forName("ISO-8859-1");
	private boolean databaseUpdateScheduled = false; ///< a flag informing whether database needs an update 
	
	FinancialBalance()
	{
		expenses = new LinkedList<Expense>();
		openExpensesFile();
		Collections.sort(expenses);
		
		monthlyReports = new TreeMap<>(new ReverseDateComparator());
		generateMonthlyReports();
	}
	
	/**
	 * Opens the database file or creates a new one.
	 */
	private void openExpensesFile()
	{
		expensesFile = Paths.get(expensesFilePath);
		if (Files.exists(expensesFile))
		{
			// Read all expenses from the .txt file
			try {
				List<String> expensesStrings = Files.readAllLines(expensesFile, charset);
				if (expensesStrings.size() > 0) {
					readExpensesFromFile(expensesStrings);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} 
		}
		else
		{
			try 
			{
				expensesFile = Files.createFile(expensesFile);
			} 
			catch (FileAlreadyExistsException fae) 
			{
				fae.printStackTrace();
			}
			catch (IOException ioe) 
			{
				ioe.printStackTrace();
			}
			catch (UnsupportedOperationException uoe) 
			{
				uoe.printStackTrace();
			}
			catch (SecurityException se) 
			{
				se.printStackTrace();
			}
		}
	}

	/**
	 * Reads database content from a list of lines (strings) to the internal container.
	 * @param expensesStrings
	 */
	private void readExpensesFromFile(List<String> expensesStrings) {
		String[] expenseString = new String[4];
		for (String s : expensesStrings) {
			Expense expense = new Expense();
			expenseString = s.split("/");
			expense.setName(expenseString[0]);
			expense.setCategory(ExpenseCategory.valueOf(expenseString[1]));
			try {
				//expense.setDate(DateFormat.getDateInstance().parse(expenseString[2]));
				Calendar date = Calendar.getInstance();
				date.setTime(new Date(Long.parseLong(expenseString[2])));
				expense.setDate(date);
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			}
			expense.setPrice(new BigDecimal(expenseString[3]));
			expenses.add(expense);
		}
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
			try {
				Files.deleteIfExists(expensesFile);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			for (Expense expense : expenses) {
				try {
					Files.write(expensesFile, expense.toDatabaseString().getBytes(charset), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} 
			}

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
		try {
			Files.deleteIfExists(expensesFile);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
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
	
	// helper comparator class
	// The months are sorted in descending order for GUI purposes.
	private class ReverseDateComparator implements Comparator<YearMonth>
	{
		@Override
		public int compare(YearMonth month1, YearMonth month2) {
			if (month1.getYear() > month2.getYear()) return -1;
			else if (month1.getYear() < month2.getYear()) return 1;
			else return -month1.compareTo(month2);
		}
	}
}
