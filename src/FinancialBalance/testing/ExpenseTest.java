package FinancialBalance.testing;

import java.text.ParseException;
import java.util.Calendar;

import org.junit.Test;

import FinancialBalance.Expense;
import FinancialBalance.ExpenseCategory;
import junit.framework.TestCase;

public class ExpenseTest extends TestCase {
	
	@Test
	public void testParseExpense()
	{
		String expenseName = "test";
		String expenseCategory = ExpenseCategory.Medicine.toString();
		String expenseDate = "2017/05/25";
		String expensePrice = "4.25";
		String dateFormat = "yyyy/MM/dd";
		Expense parsedExpense = null;
		try {
			parsedExpense = Expense.parseExpense(expenseName, expenseCategory, expenseDate, expensePrice, dateFormat);
		} catch (NumberFormatException nfe) {
			fail(nfe.getMessage());
		} catch (ParseException pe) {
			fail(pe.getMessage());
		}
		assertEquals(expenseName,parsedExpense.getName());
		assertEquals(ExpenseCategory.valueOf(expenseCategory), parsedExpense.getCategory());
		assertEquals(25, parsedExpense.getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(4, parsedExpense.getDate().get(Calendar.MONTH));
		assertEquals(2017, parsedExpense.getDate().get(Calendar.YEAR));
		assertEquals(expensePrice, parsedExpense.getPrice().toString());		
	}
}
