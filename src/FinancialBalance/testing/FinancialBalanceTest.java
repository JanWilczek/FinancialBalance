package FinancialBalance.testing;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;

import junit.framework.TestCase;
import org.junit.Test;

import FinancialBalance.Expense;
import FinancialBalance.ExpenseCategory;
import FinancialBalance.FinancialBalance;

public class FinancialBalanceTest extends TestCase {
		FinancialBalance financialBalance;
		
		protected void setUp()
		{
			financialBalance = new FinancialBalance("test");
		}
		
		protected void tearDown() 
		{
			financialBalance.clearDatabase();
			financialBalance.close();
			try {
				Files.deleteIfExists(Paths.get(financialBalance.getDatabaseFileName()));
			} catch (IOException ioe) {
				System.err.println(ioe.getMessage());
			}
		}
		
		@Test
		public void testAddExpense()
		{
			Expense expenseToAdd = new Expense("test", ExpenseCategory.School, Calendar.getInstance(), new BigDecimal("43.25"));
			financialBalance.addExpense(expenseToAdd);
			
			assertTrue(financialBalance.getExpenses().size() == 1);
			
			Expense expenseToAdd2 = new Expense("test2", ExpenseCategory.Other, Calendar.getInstance(), new BigDecimal("21.09"));
			financialBalance.addExpense(expenseToAdd2);
			
			assertTrue(financialBalance.getExpenses().size() == 2);
		}
		
		@Test
		public void testDeleteExpenseByExpense()
		{
			Expense testExpense = new Expense("test", ExpenseCategory.Medicine, Calendar.getInstance(), new BigDecimal("43.25"));
			financialBalance.addExpense(testExpense);
			Expense expenseToAdd2 = new Expense("test2", ExpenseCategory.Other, Calendar.getInstance(), new BigDecimal("21.09"));
			financialBalance.addExpense(expenseToAdd2);
			
			financialBalance.deleteExpense(testExpense);
			
			assertTrue(financialBalance.getExpenses().size() == 1);
		}
		
		@Test
		public void testDeleteExpenseByIndex()
		{
			Expense testExpense = new Expense("test", ExpenseCategory.Medicine, Calendar.getInstance(), new BigDecimal("43.25"));
			financialBalance.addExpense(testExpense);
			Expense expenseToAdd2 = new Expense("test2", ExpenseCategory.Other, Calendar.getInstance(), new BigDecimal("21.09"));
			financialBalance.addExpense(expenseToAdd2);
			
			financialBalance.deleteExpense(1);
			
			assertTrue(financialBalance.getExpenses().size() == 1);
		}
		
		@Test
		public void testClearDatabase()
		{
			Expense expenseToAdd = new Expense("test", ExpenseCategory.School, Calendar.getInstance(), new BigDecimal("43.25"));
			financialBalance.addExpense(expenseToAdd);			
			Expense expenseToAdd2 = new Expense("test2", ExpenseCategory.Other, Calendar.getInstance(), new BigDecimal("21.09"));
			financialBalance.addExpense(expenseToAdd2);
			
			financialBalance.clearDatabase();
			assertTrue(financialBalance.getExpenses().size() == 0);
		}
}
