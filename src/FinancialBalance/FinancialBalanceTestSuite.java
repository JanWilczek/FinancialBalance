package FinancialBalance;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.TestSuite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	FinancialBalanceTest.class,
	MonthlyReportTest.class,
	TextfileDataProvider.class
})

public class FinancialBalanceTestSuite extends TestSuite {

}
