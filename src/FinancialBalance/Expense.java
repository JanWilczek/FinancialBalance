package FinancialBalance;

import java.math.BigDecimal;
import java.util.Date;

class Expense {
	private String name;
	private ExpenseCategory category;
	private Date date;
	private BigDecimal price;
	
	
	// Constructors
	Expense(String name, ExpenseCategory category, Date date, BigDecimal price)	//TODO: Change Date to Calendar or an other class
	{
		this.name = name;
		this.category = category;
		this.date = date;
		this.price = price;
	}
	
	Expense(String name, Date date, BigDecimal price)
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
		this.date = new Date();
		this.price = new BigDecimal("0.00");
	}
	
	// Public member functions
	@Override
	public String toString()
	{
		return getName() + " " + getCategory() + " " + getDate() + " " + getPrice() + System.lineSeparator(); //TODO: insert a cross-platform line separator
	}
	
	public String toDatabaseString()
	{
		return getName() + "/" + getCategory() + "/" + getDate().getTime() + "/" + getPrice() + System.lineSeparator(); //TODO: insert a cross-platform line separator
	}
	
	// Getters
	public Date getDate()
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
	void setDate(Date date)
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
