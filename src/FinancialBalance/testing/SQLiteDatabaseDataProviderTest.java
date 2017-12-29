package FinancialBalance.testing;

import FinancialBalance.Expense;
import FinancialBalance.ExpenseCategory;
import FinancialBalance.SQLiteDatabaseDataProvider;
import junit.framework.TestCase;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

public class SQLiteDatabaseDataProviderTest extends TestCase {
	
	private SQLiteDatabaseDataProvider sqliteDatabaseDataProvider;
	
	public SQLiteDatabaseDataProviderTest() {}
	
	protected void setUp(){
		sqliteDatabaseDataProvider = new SQLiteDatabaseDataProvider("test.db");
	}
	
	protected void tearDown(){
		sqliteDatabaseDataProvider.close();
		try {
			Files.deleteIfExists(Paths.get(sqliteDatabaseDataProvider.getDatabaseFileName()));
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
	}
	
	@Test
	public void testNonExistentDatabase(){
		
	}
	
	@Test
	public void testGetExpenses(){
		List<Expense> expensesFromDatabase = sqliteDatabaseDataProvider.getExpenses();
		assertTrue(expensesFromDatabase != null);
		assertTrue(expensesFromDatabase.size() == 0);
	}
	
	@Test
	public void testAddExpense()
	{
		Calendar testDate = Calendar.getInstance();
		Expense expenseToAdd = new Expense("Test expense", ExpenseCategory.valueOf("Other"), testDate, new BigDecimal("69.69"));
		sqliteDatabaseDataProvider.addExpense(expenseToAdd);
		List<Expense> expenses = sqliteDatabaseDataProvider.getExpenses();
		assertTrue(expenses.size() == 1);
		Expense addedExpense = expenses.get(0);
		assertTrue(addedExpense.getName().equals("Test expense"));
		assertTrue(addedExpense.getCategory()==ExpenseCategory.Other);
		assertTrue(addedExpense.getDate().equals(testDate));
		assertTrue(addedExpense.getPrice().equals(new BigDecimal("69.69")));
	}
}
