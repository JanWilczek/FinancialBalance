package FinancialBalance;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDatabaseDataProvider implements DataProvider {
	
	private String databaseFileName;
	private String databaseTableName;
	private final String urlPrefix = "jdbc:sqlite:";
	private String url;
	
	private Connection connection;
	
	public SQLiteDatabaseDataProvider(String databaseFileName)
	{
		if (databaseFileName.endsWith(".db"))
		{
			this.databaseFileName = databaseFileName;
			this.databaseTableName = databaseFileName.substring(0, databaseFileName.length()-3);
		}
		else
		{
			databaseTableName = databaseFileName;
			this.databaseFileName = databaseFileName + ".db";
		}
		
		this.url = this.urlPrefix + this.databaseFileName;
		connect();
		createTable();
	}
	
	@Override
	public List<Expense> getExpenses() {
		List<Expense> expensesInDatabase = new LinkedList<Expense>();
		
		final String selectAllQuery = "SELECT * FROM " + this.databaseTableName + ";";
		if (connection != null)
		{
			try {
				Statement selectAllStatement = connection.createStatement();
				ResultSet expensesSet = selectAllStatement.executeQuery(selectAllQuery);
				
				while (expensesSet.next()){
					Expense expense = new Expense();
					expense.setName(expensesSet.getString(ExpenseDatabaseEntry.COLUMN_NAME));
					expense.setCategory(ExpenseCategory.valueOf(expensesSet.getString(ExpenseDatabaseEntry.COLUMN_CATEGORY)));
					Calendar date = Calendar.getInstance();
					date.setTime(new Date(expensesSet.getLong(ExpenseDatabaseEntry.COLUMN_DATE)));
					expense.setDate(date);
					expense.setPrice(new BigDecimal(expensesSet.getString(ExpenseDatabaseEntry.COLUMN_PRICE)));
					
					expensesInDatabase.add(expense);
				}
			} catch (SQLException se) {
				System.err.println(se.getMessage());
			}
		}

		return expensesInDatabase;
	}

	@Override
	public boolean addExpense(Expense expenseToAdd) {
		int insertedRows = 0;
		
		final String insertExpenseCommand = "INSERT INTO "+ this.databaseTableName + "("
				+ ExpenseDatabaseEntry.COLUMN_NAME + ","
				+ ExpenseDatabaseEntry.COLUMN_CATEGORY + ","
				+ ExpenseDatabaseEntry.COLUMN_DATE + ","
				+ ExpenseDatabaseEntry.COLUMN_PRICE + ")"
				+ " VALUES(?,?,?,?);";
		if (connection != null)
		{
			try {
				PreparedStatement insertExpenseStatement = connection.prepareStatement(insertExpenseCommand);
				insertExpenseStatement.setString(1, expenseToAdd.getName());
				insertExpenseStatement.setString(2, expenseToAdd.getCategory().toString());
				insertExpenseStatement.setLong(3, expenseToAdd.getDate().getTime().getTime());
				insertExpenseStatement.setString(4, expenseToAdd.getPrice().toString());
				insertedRows = insertExpenseStatement.executeUpdate();
			} catch (SQLException se) {
				System.err.println(se.getMessage());
			}
		}
		
		return insertedRows == 1;
	}

	@Override
	public boolean deleteExpense(Expense expenseToDelete) {
		final String deleteExpenseCommand = "DELETE FROM " + this.databaseTableName + " WHERE "
				+ ExpenseDatabaseEntry.COLUMN_NAME + " = \"" + expenseToDelete.getName() + "\" AND "
				+ ExpenseDatabaseEntry.COLUMN_CATEGORY + " = \"" + expenseToDelete.getCategory().toString() + "\" AND "
				+ ExpenseDatabaseEntry.COLUMN_DATE + " = " + expenseToDelete.getDate().getTime().getTime() + " AND "
				+ ExpenseDatabaseEntry.COLUMN_PRICE + " = \"" + expenseToDelete.getPrice().toString() + "\"";
		
		
		try {
			Statement deleteExpenseStatement = connection.createStatement();
			deleteExpenseStatement.executeUpdate(deleteExpenseCommand);
			return true;
		} catch (SQLException se) {
			System.err.println(se.getMessage());
			return false;
		}
	}

	@Override
	public void clearDatabase() {
		final String clearDatabaseCommand = "DROP TABLE IF EXISTS " + this.databaseTableName + ";";
		
		try {
			Statement deleteExpenseStatement = connection.createStatement();
			deleteExpenseStatement.execute(clearDatabaseCommand);
		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
		createTable();
	}
	
	@Override
	public void updateDatabase(List<Expense> expenses) {
		clearDatabase();
		createTable();
		for (Expense expense : expenses)
		{
			addExpense(expense);
		}
	}

	@Override
	public void close() {
		disconnect();
	}
	
	@Override
	public String getDatabaseFileName() { return this.databaseFileName; }
	public String getDatabaseTableName() { return this.databaseTableName; }
	
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
		final String createTableCommand = "CREATE TABLE IF NOT EXISTS " + this.databaseTableName + "(\n"
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
	
	/**
	 * 
	 * @author Jan F. Wilczek
	 * A static class holding the data necessary for database communication.
	 */
	public static final class ExpenseDatabaseEntry
	{
		public static final String _ID = "id";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_CATEGORY = "category";
		public static final String COLUMN_DATE = "date";
		public static final String COLUMN_PRICE = "price";
	}
	
}
