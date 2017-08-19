package FinancialBalance;

enum ExpenseCategory {
	Other(0), 
	School(1),
	Medicine(2),
	Meals(3),
	Shopping(4),
	Transport(5);
	
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
