package FinancialBalance.testing;

import junit.framework.TestCase;

import java.util.List;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.LinkedList;

import org.junit.Test;

import FinancialBalance.Expense;
import FinancialBalance.ExpenseCategory;
import FinancialBalance.MonthlyReport;

public class MonthlyReportTest extends TestCase {
	
	private List<Expense> mockExpenses;
	
	protected void setUp()
	{
		mockExpenses = new LinkedList<Expense>();
		
		Calendar testDate1 = Calendar.getInstance();
		Calendar testDate2 = Calendar.getInstance();
		Calendar testDate3 = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try {
			testDate1.setTime(sdf.parse("03-12-2017"));
			testDate2.setTime(sdf.parse("28-12-2017"));
			testDate3.setTime(sdf.parse("13-11-2017"));
		} catch (ParseException pe) {
			System.err.println(pe.getMessage());
		}
		mockExpenses.add(new Expense("test expense 1", ExpenseCategory.Medicine, testDate1, new BigDecimal("3.45") ));
		mockExpenses.add(new Expense("test expense 4", ExpenseCategory.Other, testDate2, new BigDecimal("60.24")));
		mockExpenses.add(new Expense("test expense 2", ExpenseCategory.School, testDate2, new BigDecimal("2.43")));
		mockExpenses.add(new Expense("test expense 3", ExpenseCategory.School, testDate3, new BigDecimal("16.08")));
	}
	
	@Test
	public void testMonthlyReports()
	{
		BigDecimal total = new BigDecimal("3.45");
		total = total.add(new BigDecimal("60.24"));
		total = total.add(new BigDecimal("2.43"));
		
		MonthlyReport testReport = new MonthlyReport(YearMonth.of(2017, 12), mockExpenses);
		assertTrue(testReport.getTotal().equals(total));
		assertTrue(testReport.getCategoryTotal(ExpenseCategory.School).equals(new BigDecimal("2.43")));
	}
}
