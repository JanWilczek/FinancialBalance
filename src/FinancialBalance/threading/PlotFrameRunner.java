package FinancialBalance.threading;

import java.time.YearMonth;
import java.util.Map;

import FinancialBalance.MonthlyReport;
import FinancialBalance.StatisticsFrame;

/**
 * 
 * @author Jan F. Wilczek
 * @date 19.11.17
 * @version 1.0
 * 
 *	A threading utility used for displaying statistics window in a separate thread.
 */
public class PlotFrameRunner implements Runnable
{
	private Map<YearMonth, MonthlyReport> monthlyReports;
	
	public PlotFrameRunner(Map<YearMonth, MonthlyReport> monthlyReports)
	{
		this.monthlyReports = monthlyReports;
	}		
	
	@Override
	public void run() {
		@SuppressWarnings("unused")
		StatisticsFrame statisticsFrame = new StatisticsFrame(monthlyReports);
	}
}
