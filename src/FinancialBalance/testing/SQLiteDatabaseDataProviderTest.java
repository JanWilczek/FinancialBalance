package FinancialBalance.testing;

import FinancialBalance.Expense;
import FinancialBalance.ExpenseCategory;
import FinancialBalance.SQLiteDatabaseDataProvider;
import junit.framework.TestCase;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class SQLiteDatabaseDataProviderTest extends TestCase {
	
	private SQLiteDatabaseDataProvider sqliteDatabaseDataProvider;
	private Expense testExpense1;
	private Expense testExpense2;
	private Expense testExpense3;
	
	protected void setUp(){
		sqliteDatabaseDataProvider = new SQLiteDatabaseDataProvider("test");
		
		Calendar testDate1 = Calendar.getInstance();
		Calendar testDate2 = Calendar.getInstance();
		Calendar testDate3 = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try {
			testDate1.setTime(sdf.parse("01-03-2011"));
			testDate2.setTime(sdf.parse("28-12-2017"));
			testDate3.setTime(sdf.parse("13-05-2014"));
		} catch (ParseException pe) {
			System.err.println(pe.getMessage());
		}
		testExpense1 = new Expense("test expense 1", ExpenseCategory.Medicine, testDate1, new BigDecimal("3.45") );
		testExpense2 = new Expense("test expense 2", ExpenseCategory.School, testDate2, new BigDecimal("2.43"));
		testExpense3 = new Expense("test expense 3", ExpenseCategory.Meals, testDate3, new BigDecimal("16.08"));
		
		sqliteDatabaseDataProvider.addExpense(testExpense1);
		sqliteDatabaseDataProvider.addExpense(testExpense2);
		sqliteDatabaseDataProvider.addExpense(testExpense3);
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
	public void testGetExpenses(){
		List<Expense> expensesFromDatabase = sqliteDatabaseDataProvider.getExpenses();
		assertTrue(expensesFromDatabase != null);
		assertTrue(expensesFromDatabase.size() == 3);
	}
	
	@Test
	public void testAddExpense()
	{
		Calendar testDate = Calendar.getInstance();	// Should be the newest one in the database
		Expense expenseToAdd = new Expense("Test expense", ExpenseCategory.Other, testDate, new BigDecimal("69.69"));
		sqliteDatabaseDataProvider.addExpense(expenseToAdd);
		List<Expense> expenses = sqliteDatabaseDataProvider.getExpenses();
		assertTrue(expenses.size() == 4);
		Expense addedExpense = expenses.get(3);
		assertTrue(addedExpense.getName().equals("Test expense"));
		assertTrue(addedExpense.getCategory()==ExpenseCategory.Other);
		assertTrue(addedExpense.getDate().equals(testDate));
		assertTrue(addedExpense.getPrice().equals(new BigDecimal("69.69")));
	}
	
	@Test
	public void testDoubleAddExpense()
	{
		Calendar testDate = Calendar.getInstance();
		Expense expenseToAdd = new Expense("Test expense", ExpenseCategory.Other, testDate, new BigDecimal("69.69"));
		sqliteDatabaseDataProvider.addExpense(expenseToAdd);
		sqliteDatabaseDataProvider.addExpense(expenseToAdd);
		
		assertTrue(sqliteDatabaseDataProvider.getExpenses().size() == 5);
	}
	
	@Test
	public void testDeleteExpense()
	{
		sqliteDatabaseDataProvider.deleteExpense(testExpense1);
		
		List<Expense> expensesAfterDelete = sqliteDatabaseDataProvider.getExpenses();
		
		assertTrue(expensesAfterDelete.size() == 2);
		assertTrue(expensesAfterDelete.contains(testExpense2));
		assertTrue(expensesAfterDelete.contains(testExpense3));
	}
	
	@Test
	public void testUpdateDatabase()
	{	
		List<Expense> expensesForUpdate = new LinkedList<Expense>();
		expensesForUpdate.add(testExpense2);
		expensesForUpdate.add(testExpense3);
		
		sqliteDatabaseDataProvider.updateDatabase(expensesForUpdate);
		
		List<Expense> expensesAfterUpdate = sqliteDatabaseDataProvider.getExpenses();
		assertTrue(expensesAfterUpdate.equals(expensesForUpdate));
	}
	
	@Test
	public void testClearDatabase()
	{
		sqliteDatabaseDataProvider.clearDatabase();
		assertTrue(sqliteDatabaseDataProvider.getExpenses().size() == 0);
	}
}
