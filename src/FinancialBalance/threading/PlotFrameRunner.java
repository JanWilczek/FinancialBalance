package FinancialBalance.threading;

import java.time.YearMonth;
import java.util.Map;

import FinancialBalance.BarChartFrame;
import FinancialBalance.MonthlyReport;
import FinancialBalance.PlotFrame;

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
		PlotFrame plotFrame = new PlotFrame(monthlyReports);
		@SuppressWarnings("unused")
		BarChartFrame barChartFrame = new BarChartFrame(monthlyReports);
	}
}
