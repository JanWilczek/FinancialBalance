package FinancialBalance;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.lang.Comparable;

/**
 * Class representing an expense.
 * @author Jan Wilczek
 * @version 1.0
 */
public class Expense implements Comparable<Expense> {
	private String name;
	private ExpenseCategory category;
	private Calendar date;
	private BigDecimal price;
	
	
	// Constructors
	/**
	 * Creates an expense with the given parameters.
	 * @param name
	 * @param category
	 * @param date
	 * @param price
	 */
	public Expense(String name, ExpenseCategory category, Calendar date, BigDecimal price)
	{
		this.name = name;
		this.category = category;
		this.date = date;
		this.price = price;
	}
	
	/**
	 * Creates an expense with the given parameters. Category defaults to <code>ExpenseCategory.Other</code>.
	 * @param name
	 * @param date
	 * @param price
	 */
	public Expense(String name, Calendar date, BigDecimal price)
	{
		this.name = name;
		this.category = ExpenseCategory.Other;
		this.date = date;
		this.price = price;
	}
	
	/**
	 * Creates an expense with an empty name, category equal to <code>ExpenseCategory.Other</code>, current date and price equal to 0.
	 */
	public Expense()
	{
		this.name = "";
		this.category = ExpenseCategory.Other;
		this.date = Calendar.getInstance();
		this.price = new BigDecimal("0.00");
	}
	
	// Public member functions
	@Override
	public int compareTo(Expense expense)
	{
		if (!this.getDate().getTime().equals(expense.getDate().getTime())) return -this.getDate().getTime().compareTo(expense.getDate().getTime());	// The first expense to be stored is the newest one (for GUI purposes). It's not a problem with LinekdList.
		if (!this.getPrice().equals(expense.getPrice())) return this.getPrice().compareTo(expense.getPrice());
		return this.getName().compareTo(expense.getName());
	}
	
	@Override
	public boolean equals(Object o)
	{
		Expense expense = (Expense) o;
		Calendar cal = expense.getDate();
		boolean isDateEqual = cal.get(Calendar.YEAR)==this.date.get(Calendar.YEAR) && cal.get(Calendar.MONTH)==this.date.get(Calendar.MONTH) && cal.get(Calendar.DAY_OF_MONTH)==this.date.get(Calendar.DAY_OF_MONTH);
		return isDateEqual && this.getPrice().equals(expense.getPrice()) && this.getCategory().equals(expense.getCategory()) && this.getName().equals(expense.getName());
	}
	
	@Override
	public String toString()
	{
		return getName() + " " + getCategory() + " " + getDate() + " " + getPrice();
	}
	
	/**
	 * @param fieldSeparator
	 * @return String with all fields' values separated by the given separator 
	 */
	public String toDatabaseString(String fieldSeparator)
	{
		return getName() + fieldSeparator + getCategory() + fieldSeparator + getDate().getTime().getTime() + fieldSeparator + getPrice();
	}
	
	// Getters
	public Calendar getDate()
	{
		return date;
	}
	
	public String getName()
	{
		return name;
	}
	
	public ExpenseCategory getCategory()
	{
		return category;
	}
	
	public BigDecimal getPrice()
	{
		return price;
	}
	
	// Setters
	public void setDate(Calendar date)
	{
		this.date = date;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setCategory(ExpenseCategory category)
	{
		this.category = category;
	}
	
	public void setPrice(BigDecimal price)
	{
		this.price = price;
	}
	
	// static methods
	public static Expense parseExpense(String expenseName, 
			String expenseCategory, 
			String expenseDate, 
			String expensePrice,  
			String dateFormat) throws ParseException, NumberFormatException
	{
		// Parse date
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		Calendar parsedDate = Calendar.getInstance();
		parsedDate.setTime(simpleDateFormat.parse(expenseDate));

		// Parse price
		BigDecimal parsedPrice = null;
		parsedPrice = new BigDecimal(expensePrice);
		
		Expense parsedExpense = new Expense(expenseName, ExpenseCategory.valueOf(expenseCategory), parsedDate, parsedPrice);
		
		return parsedExpense;
	}
}
