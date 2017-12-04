package FinancialBalance;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDatabaseDataProvider implements DataProvider {
	
	private String databaseFileName;
	private final String urlPrefix = "jdbc:sqlite:";
	private String url;
	
	private Connection connection;
	
	public SQLiteDatabaseDataProvider(String databaseFileName)
	{
		if (!databaseFileName.endsWith(".db")) databaseFileName += ".db";
		this.databaseFileName = databaseFileName;
		this.url = this.urlPrefix + this.databaseFileName;
		connect();
		createTable();
	}
	
	@Override
	public List<Expense> getExpenses() {
		List<Expense> expensesInDatabase = new LinkedList<Expense>();
		
		final String selectAllQuery = "SELECT * FROM " + ExpenseDatabaseEntry.TABLE_NAME + ";";
		if (connection != null)
		{
			try {
				Statement selectAllStatement = connection.createStatement();
				ResultSet expensesSet = selectAllStatement.executeQuery(selectAllQuery);
				
				while (expensesSet.next()){
					Expense expense = new Expense();
					expense.setName(expensesSet.getString(ExpenseDatabaseEntry.COLUMN_NAME));
					expense.setCategory(ExpenseCategory.valueOf(expensesSet.getString(ExpenseDatabaseEntry.COLUMN_CATEGORY)));
					try {
						Calendar date = Calendar.getInstance();
						date.setTime(new Date(Long.parseLong(expensesSet.getString(ExpenseDatabaseEntry.COLUMN_DATE))));
						expense.setDate(date);
					} catch (NumberFormatException nfe) {
						System.err.println(nfe.getMessage());
					}
					expense.setPrice(new BigDecimal(expensesSet.getString(ExpenseDatabaseEntry.COLUMN_PRICE)));
				}
			} catch (SQLException se) {
				System.err.println(se.getMessage());
			}
		}
		return expensesInDatabase;
	}

	@Override
	public void addExpense(Expense expenseToAdd) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean deleteExpense(Expense expenseToDelete) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteExpense(int indexExpenseToDelete) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateDatabase(List<Expense> expenses) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearDatabase() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClose() {
		disconnect();
	}
	
	public String getDatabaseFileName() { return this.databaseFileName; }
	
	private void connect()
	{
		try {
			connection = DriverManager.getConnection(url);
		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
	}
	
	private void disconnect()
	{
		if (connection != null)
		{
			try {
				connection.close();
			} catch(SQLException se) {
				System.err.println(se.getMessage());
			}
		}
	}
	
	private void createTable()
	{
		final String createTableCommand = "CREATE TABLE IF NOT EXISTS " + ExpenseDatabaseEntry.TABLE_NAME + "(\n"
				+ ExpenseDatabaseEntry._ID + " integer PRIMARY KEY,\n "
				+ ExpenseDatabaseEntry.COLUMN_NAME + " text NOT NULL,\n "
				+ ExpenseDatabaseEntry.COLUMN_CATEGORY + " text NOT NULL,\n "
				+ ExpenseDatabaseEntry.COLUMN_DATE + " integer NOT NULL,\n "
				+ ExpenseDatabaseEntry.COLUMN_PRICE + " text NOT NULL\n"
				+ ");";
		
		if (connection != null) {
			try {
				Statement createTableStatement = connection.createStatement();
				createTableStatement.execute(createTableCommand);
			} catch (SQLException se) {
				System.err.println(se.getMessage());
			} 
		}
	}
	
	public static final class ExpenseDatabaseEntry
	{
		public static final String TABLE_NAME = "expenses";
		
		public static final String _ID = "id";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_CATEGORY = "category";
		public static final String COLUMN_DATE = "date";
		public static final String COLUMN_PRICE = "price";
	}
	
}
