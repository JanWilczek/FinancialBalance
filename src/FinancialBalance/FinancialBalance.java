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
	
	FinancialBalance()
	{
		expenses = new LinkedList<Expense>();
		openExpensesFile();
		Collections.sort(expenses);
		
		monthlyReports = new TreeMap<>();
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
	 * Adds an expense to the internal container and to the database file. 
	 * @param expenseToAdd
	 */
	public void addExpense(Expense expenseToAdd)
	{
		expenses.add(expenseToAdd);
		try
		{
			Files.write(expensesFile, expenseToAdd.toDatabaseString().getBytes(charset), StandardOpenOption.CREATE, StandardOpenOption.APPEND);		//TODO: Change file input implementation
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
			System.err.println(ioe.getMessage());
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
	
	/**
	 * Deletes the given expense.
	 * @param expenseToDelete
	 */
	public boolean deleteExpense(Expense expenseToDelete)		// TODO: reimplement
	{
		expenses.remove(expenseToDelete);
		try
		{
			List<String> expensesStrings = Files.readAllLines(expensesFile);
			expensesStrings.remove(expenseToDelete.toDatabaseString());
			Files.write(expensesFile, expensesStrings, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
			
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the internal container of expenses.
	 * TODO: Reconsider the use of this method.
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
			Files.delete(expensesFile);
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
}
