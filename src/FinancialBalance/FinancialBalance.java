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
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.text.ParseException;

public class FinancialBalance {
	private List<Expense> expenses;
	private String expensesFilePath = "expenses.txt";
	private Path expensesFile;
	private final static Charset charset = Charset.forName("ISO-8859-1");
	
	FinancialBalance()
	{
		expenses = new LinkedList<Expense>();
		openExpensesFile();
	}
	
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

	private void readExpensesFromFile(List<String> expensesStrings) {
		Expense expense = new Expense();
		String[] expenseString = new String[4];
		for (String s : expensesStrings) {
			expenseString = s.split("/");
			expense.setName(expenseString[0]);
			expense.setCategory(ExpenseCategory.valueOf(expenseString[1]));
			try {
				//expense.setDate(DateFormat.getDateInstance().parse(expenseString[2]));
				expense.setDate(new Date(Long.parseLong(expenseString[2])));
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			}
			expense.setPrice(new BigDecimal(expenseString[3]));
			expenses.add(expense);
		}
	}
	
	// Public members functions
	public void addExpense(Expense expense)
	{
		expenses.add(expense);
		try
		{
			Files.write(expensesFile, expense.toDatabaseString().getBytes(charset), StandardOpenOption.CREATE, StandardOpenOption.APPEND);		//TODO: Change file input implementation
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
	
	public void deleteExpense(Expense expense)
	{
		//TODO
	}
	
	public List<Expense> getExpenses()
	{
		return expenses;
	}
	

}
