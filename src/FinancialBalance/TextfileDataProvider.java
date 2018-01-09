package FinancialBalance;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public final class TextfileDataProvider implements DataProvider {
	private String expensesFilePath;
	private Path expensesFile;
	private final static Charset charset = Charset.forName("ISO-8859-2");
	
	public TextfileDataProvider()
	{
		expensesFilePath = "expenses.txt";
	}
	
	public TextfileDataProvider(String expensesFilePath)
	{
		if (expensesFilePath.endsWith(".txt")) this.expensesFilePath = expensesFilePath;
		else this.expensesFilePath = expensesFilePath + ".txt";
		
		expensesFile = Paths.get(this.expensesFilePath);
	}
	
	@Override
	public List<Expense> getExpenses() {
		List<Expense> expenses = openExpensesFile();
		if (expenses == null) expenses = new LinkedList<Expense>();	// TODO: Unhardcode LinkedList here. Is it elegant to store it this way?
		return expenses;
	}
	
	@Override
	public void clearDatabase() {
		try {
			Files.deleteIfExists(expensesFile);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}	
	}
	
	@Override
	public void updateDatabase(List<Expense> expenses) {
			try {
				Files.deleteIfExists(expensesFile);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			for (Expense expense : expenses) {
				try {
					Files.write(expensesFile, (expense.toDatabaseString("/") + System.lineSeparator()).getBytes(charset), StandardOpenOption.CREATE,
							StandardOpenOption.APPEND);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			} 
	}
	
	@Override
	public boolean addExpense(Expense expenseToAdd) {
		try {
			Files.write(expensesFile, (expenseToAdd.toDatabaseString("/") + System.lineSeparator()).getBytes(charset), StandardOpenOption.CREATE,
					StandardOpenOption.APPEND);
			return true;
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
			return false;
		}
	}
	
	@Override
	public boolean deleteExpense(Expense expenseToDelete) {
		if (Files.exists(expensesFile))
		{
			try {
				List<String> expensesStrings = Files.readAllLines(expensesFile, charset);
				String stringToDelete = expenseToDelete.toDatabaseString("/");
				if (expensesStrings.remove(stringToDelete))	// BUG!!!
				{
					Files.delete(expensesFile);
					Files.write(expensesFile, expensesStrings, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
					return true;
				}
			} catch (IOException ioe) {
				System.err.println(ioe.getMessage());
			} 
		}
		return false;
	}
	
	@Override
	public void close() {
		List<Expense> expenses = getExpenses();
		Collections.sort(expenses);
		updateDatabase(expenses);
	}
	
	@Override
	public String getDatabaseFileName(){ return expensesFilePath; }
	
	/**
	 * Opens the database file or creates a new one.
	 */
	private List<Expense> openExpensesFile()
	{
		if (Files.exists(expensesFile))
		{
			// Read all expenses from the .txt file
			try {
				List<String> expensesStrings = Files.readAllLines(expensesFile, charset);
				if (expensesStrings.size() > 0) {
					return readExpensesFromFile(expensesStrings);
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
		return null;
	}

	/**
	 * Reads database content from a list of lines (strings) to the internal container.
	 * @param expensesStrings
	 */
	private List<Expense> readExpensesFromFile(List<String> expensesStrings) {
		List<Expense> expenses = new LinkedList<Expense>();	
		String[] expenseString = new String[4];
		for (String s : expensesStrings) {
			Expense expense = new Expense();
			expenseString = s.split("/");
			expense.setName(expenseString[0]);
			expense.setCategory(ExpenseCategory.valueOf(expenseString[1]));
			try {
				Calendar date = Calendar.getInstance();
				date.setTime(new Date(Long.parseLong(expenseString[2])));
				expense.setDate(date);
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			}
			expense.setPrice(new BigDecimal(expenseString[3]));
			expenses.add(expense);
		}
		return expenses;
	}

}
