package FinancialBalance;

import java.math.BigDecimal;
import java.util.Calendar;
import java.lang.Comparable;

/**
 * A class representing an expense.
 * @author Jan Wilczek
 */
class Expense implements Comparable<Expense> {
	private String name;
	private ExpenseCategory category;
	private Calendar date;
	private BigDecimal price;
	
	
	// Constructors
	Expense(String name, ExpenseCategory category, Calendar date, BigDecimal price)	//TODO: Change Date to Calendar or an other class
	{
		this.name = name;
		this.category = category;
		this.date = date;
		this.price = price;
	}
	
	Expense(String name, Calendar date, BigDecimal price)
	{
		this.name = name;
		this.category = ExpenseCategory.Other;
		this.date = date;
		this.price = price;
	}
	
	Expense()
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
		if (!this.getDate().getTime().equals(expense.getDate().getTime())) return this.getDate().getTime().compareTo(expense.getDate().getTime());
		if (!this.getPrice().equals(expense.getPrice())) return this.getPrice().compareTo(expense.getPrice());
		return this.getName().compareTo(expense.getName());
	}
	
	@Override
	public String toString()
	{
		return getName() + " " + getCategory() + " " + getDate() + " " + getPrice() + System.lineSeparator(); //TODO: insert a cross-platform line separator
	}
	
	public String toDatabaseString()
	{
		return getName() + "/" + getCategory() + "/" + getDate().getTime().getTime() + "/" + getPrice() + System.lineSeparator(); //TODO: insert a cross-platform line separator
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
