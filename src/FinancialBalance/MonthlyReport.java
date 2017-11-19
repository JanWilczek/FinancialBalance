/**
 * @author Jan Wilczek
 * @date 02.10.17
 * 
 * @version 1.0
 * 
 * @class MonthlyReport
 * @brief Class representing a monthly expense sum
 * 
 */

package FinancialBalance;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.time.YearMonth;
import java.math.BigDecimal;

public class MonthlyReport {
	
	public MonthlyReport(YearMonth month, List<Expense> expenses)
	{
		this.month = month;
		total = new BigDecimal("0.00");
		categoriesTotals = new HashMap<ExpenseCategory, BigDecimal>();
		for (ExpenseCategory expenseCategory : ExpenseCategory.values()) categoriesTotals.put(expenseCategory, new BigDecimal("0.00"));
		CalculateMonthlyExpenses(expenses);

	}
	
	// Public getters:
	public BigDecimal getTotal() { return total;}
	public BigDecimal getCategoryTotal(ExpenseCategory category){return categoriesTotals.get(category);}
	public YearMonth getMonth() { return month;}
	
	// Private methods
	private void CalculateMonthlyExpenses(List<Expense> expenses)
	{
		for (Expense expense : expenses)
		{
			if (expense.getDate().get(Calendar.MONTH) + 1 == month.getMonth().getValue() && expense.getDate().get(Calendar.YEAR) == month.getYear()) {
				categoriesTotals.put(expense.getCategory(), categoriesTotals.get(expense.getCategory()).add(expense.getPrice()));
				total = total.add(expense.getPrice());
			}
		}
	}
	
	// Private members:
	private BigDecimal total;
	private HashMap<ExpenseCategory, BigDecimal> categoriesTotals;
	private YearMonth month;
}
