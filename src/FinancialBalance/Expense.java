package FinancialBalance;

import java.math.BigDecimal;
import java.util.Calendar;
import java.lang.Comparable;

/**
 * A class representing an expense.
 * @author Jan Wilczek
 */
public class Expense implements Comparable<Expense> {
	private String name;
	private ExpenseCategory category;
	private Calendar date;
	private BigDecimal price;
	
	
	// Constructors
	public Expense(String name, ExpenseCategory category, Calendar date, BigDecimal price)	//TODO: Change Date to Calendar or an other class
	{
		this.name = name;
		this.category = category;
		this.date = date;
		this.price = price;
	}
	
	public Expense(String name, Calendar date, BigDecimal price)
	{
		this.name = name;
		this.category = ExpenseCategory.Other;
		this.date = date;
		this.price = price;
	}
	
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
		return getName() + " " + getCategory() + " " + getDate() + " " + getPrice() + System.lineSeparator();
	}
	
	public String toDatabaseString()
	{
		return getName() + "/" + getCategory() + "/" + getDate().getTime().getTime() + "/" + getPrice() + System.lineSeparator();
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
	void setDate(Calendar date)
	{
		this.date = date;
	}
	
	void setName(String name)
	{
		this.name = name;
	}
	
	void setCategory(ExpenseCategory category)
	{
		this.category = category;
	}
	
	void setPrice(BigDecimal price)
	{
		this.price = price;
	}
	
	// Other methods
}
