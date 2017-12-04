package FinancialBalance.testing;

import FinancialBalance.Expense;
import FinancialBalance.SQLiteDatabaseDataProvider;
import junit.framework.TestCase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

public class SQLiteDatabaseDataProviderTest extends TestCase {
	
	private SQLiteDatabaseDataProvider sqliteDatabaseDataProvider;
	
	public SQLiteDatabaseDataProviderTest() {}
	
	protected void setUp(){
		sqliteDatabaseDataProvider = new SQLiteDatabaseDataProvider("test.db");
	}
	
	protected void tearDown(){
		sqliteDatabaseDataProvider.onClose();
		try {
			Files.deleteIfExists(Paths.get(sqliteDatabaseDataProvider.getDatabaseFileName()));
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
	}
	
	@Test
	public void testNonExistentDatabase(){
		
	}
	
	@Test
	public void testGetExpenses(){
		List<Expense> expensesFromDatabase = sqliteDatabaseDataProvider.getExpenses();
		assertTrue(expensesFromDatabase != null);
		assertTrue(expensesFromDatabase.size() == 0);
	}
}
