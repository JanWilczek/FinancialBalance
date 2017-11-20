package FinancialBalance;

import java.time.YearMonth;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * 
 * @author Jan F. Wilczek
 * @date 20.11.17
 * 
 * @version 1.0
 * 
 * Factory class for creating specific graphs based on passed monthly reports.
 * 
 */
public class StatisticsChartPanelFactory {
	
	/**
	 * Creates a JFreeChart line plot of monthly totals enclosed in a ChartPanel
	 * @param monthlyReports
	 * @return chartPanel
	 *  the created ChartPanel with line plot
	 */
	public static ChartPanel createLinePlotPanel(Map<YearMonth, MonthlyReport> monthlyReports){
		DefaultCategoryDataset totalsDataset = new DefaultCategoryDataset();
		for (Map.Entry<YearMonth,MonthlyReport> entry : monthlyReports.entrySet())
			totalsDataset.addValue(entry.getValue().getTotal(), "Total", entry.getKey().toString());
		JFreeChart lineChart = ChartFactory.createLineChart("Expenses history", "Months", "Totals", totalsDataset, PlotOrientation.VERTICAL, true, true, false);
		ChartPanel chartPanel = new ChartPanel(lineChart);
		return chartPanel;
	}
	
	/**
	 * Creates a JFreeChart bar chart of categories' totals from a specific month enclosed in a ChartPanel
	 * 
	 * @param monthlyReports
	 * @return chartPanel
	 *  the created ChartPanel with bar chart
	 *  
	 */
	public static ChartPanel createBarChartPanel(Map<YearMonth, MonthlyReport> monthlyReports, YearMonth month){
		DefaultCategoryDataset monthTotalsDataset = new DefaultCategoryDataset();
		ExpenseCategory[] categories = ExpenseCategory.values();
		MonthlyReport report = monthlyReports.get(month);
		for (ExpenseCategory category : categories)
			monthTotalsDataset.addValue(report.getCategoryTotal(category),category,month);
		JFreeChart barChart = ChartFactory.createBarChart("Monthly expenses by category", "Month", "Totals", monthTotalsDataset, PlotOrientation.VERTICAL, true, true, false);
		ChartPanel chartPanel = new ChartPanel(barChart);
		return chartPanel;
	}
}
