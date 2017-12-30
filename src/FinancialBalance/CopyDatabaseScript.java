package FinancialBalance;

public class CopyDatabaseScript {

	public static void main(String[] args) {
		
		if (args.length != 2)
		{
			System.err.println("Usage: java CopyDatabaseScript source.txt target.db");
			return;
		}
		
		DataProvider textfileDataProvider = new TextfileDataProvider(args[0]);
		DataProvider sqliteDatabaseDataProvider = new SQLiteDatabaseDataProvider(args[1]);
		
		sqliteDatabaseDataProvider.updateDatabase(textfileDataProvider.getExpenses());
		
		System.out.println("Successfully copied expenses from " + args[0] + " to " + args[1] + ".");
	}

}
