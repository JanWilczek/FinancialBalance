package FinancialBalance.testing;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class FinancialBalanceTestRunner {
	public static void main(String[] args)
	{
		Result testResult = JUnitCore.runClasses(FinancialBalanceTestSuite.class);
			
		for (Failure failure : testResult.getFailures())
			System.out.println(failure.getMessage());
			
		if (testResult.wasSuccessful()) System.out.println("All tests successfully completed!");
		else System.out.println("Tests failed!");
	}
}
