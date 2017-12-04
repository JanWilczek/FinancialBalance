package FinancialBalance;

public enum ExpenseCategory {
	Other(0), 	// Categories are sorted by popularity apart from "Other" which should be always shown as first.
	Food(1),
	Meals(2),
	Transport(3),
	Medicine(4),
	School(5);
	
	int value;
	
	ExpenseCategory(int value)
	{
		this.value = value;
	}
	
	ExpenseCategory(String name)
	{
		this.value = ExpenseCategory.valueOf(name).ordinal();
	}
	
	@Override
	public String toString()
	{
		return this.name();
	}
	
}
