package FinancialBalance.testing;

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

import FinancialBalance.Expense;
import FinancialBalance.ExpenseCategory;
import FinancialBalance.TextfileDataProvider;

public class TextfileDataProviderTest extends TestCase {
	private TextfileDataProvider textfileDataProvider;
	private Expense testExpense1;
	private Expense testExpense2;
	private Expense testExpense3;
	
	protected void setUp(){
		textfileDataProvider = new TextfileDataProvider("test");
		
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
		
		textfileDataProvider.addExpense(testExpense1);
		textfileDataProvider.addExpense(testExpense2);
		textfileDataProvider.addExpense(testExpense3);
	}
	
	protected void tearDown(){
		textfileDataProvider.close();
		try {
			Files.deleteIfExists(Paths.get(textfileDataProvider.getDatabaseFileName()));
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
	}
	
	@Test
	public void testGetExpenses(){
		List<Expense> expensesFromDatabase = textfileDataProvider.getExpenses();
		assertTrue(expensesFromDatabase != null);
		assertTrue(expensesFromDatabase.size() == 3);
	}
	
	@Test
	public void testAddExpense()
	{
		Calendar testDate = Calendar.getInstance();	// Should be the newest one in the database
		Expense expenseToAdd = new Expense("Test expense", ExpenseCategory.Other, testDate, new BigDecimal("69.69"));
		textfileDataProvider.addExpense(expenseToAdd);
		List<Expense> expenses = textfileDataProvider.getExpenses();
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
		textfileDataProvider.addExpense(expenseToAdd);
		textfileDataProvider.addExpense(expenseToAdd);
		
		assertTrue(textfileDataProvider.getExpenses().size() == 5);
	}
	
	@Test
	public void testDeleteExpense()
	{
		textfileDataProvider.deleteExpense(testExpense1);
		
		List<Expense> expensesAfterDelete = textfileDataProvider.getExpenses();
		
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
		
		textfileDataProvider.updateDatabase(expensesForUpdate);
		
		List<Expense> expensesAfterUpdate = textfileDataProvider.getExpenses();
		assertTrue(expensesAfterUpdate.equals(expensesForUpdate));
	}
	
	@Test
	public void testClearDatabase()
	{
		textfileDataProvider.clearDatabase();
		assertTrue(textfileDataProvider.getExpenses().size() == 0);
	}
}
