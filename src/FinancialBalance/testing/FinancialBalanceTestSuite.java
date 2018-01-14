package FinancialBalance.testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	FinancialBalanceTest.class,
	MonthlyReportTest.class,
	TextfileDataProviderTest.class,
	SQLiteDatabaseDataProviderTest.class,
	ExpenseTest.class
})
public class FinancialBalanceTestSuite {}
