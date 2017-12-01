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
	 * @param includeTotal
	 * @param categoriesToDisplay
	 * 
	 * @return chartPanel
	 *  the created ChartPanel with line plot
	 */
	public static ChartPanel createLinePlotPanel(Map<YearMonth, MonthlyReport> monthlyReports, boolean includeTotal, ExpenseCategory[] categoriesToDisplay){
		// Create the dataset
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		if (includeTotal) {
			for (Map.Entry<YearMonth, MonthlyReport> entry : monthlyReports.entrySet())
				dataset.addValue(entry.getValue().getTotal(), "Total", entry.getKey().toString());
		}
		if (categoriesToDisplay != null)
		{
			for (ExpenseCategory category : categoriesToDisplay)
				for (Map.Entry<YearMonth, MonthlyReport> entry : monthlyReports.entrySet())
					dataset.addValue(entry.getValue().getCategoryTotal(category), category.toString(), entry.getKey().toString());
		}
		
		// Create the chart
		JFreeChart timeLineChart = ChartFactory.createLineChart("Expenses history", "Months", "Categories' totals", dataset);
		ChartPanel chartPanel = new ChartPanel(timeLineChart);
		return chartPanel;
	}
	
	/**
	 * Creates a JFreeChart bar chart of categories' totals from a specific month enclosed in a ChartPanel
	 * 
	 * @param monthlyReports
	 * @param monthToDisplay
	 * 
	 * @return chartPanel
	 *  the created ChartPanel with bar chart
	 *  
	 */
	public static ChartPanel createBarChartPanel(Map<YearMonth, MonthlyReport> monthlyReports, YearMonth monthToDisplay){
		DefaultCategoryDataset monthTotalsDataset = new DefaultCategoryDataset();
		ExpenseCategory[] categories = ExpenseCategory.values();
		MonthlyReport report = monthlyReports.get(monthToDisplay);
		if (report != null) {
			for (ExpenseCategory category : categories)
				monthTotalsDataset.addValue(report.getCategoryTotal(category), category, monthToDisplay);
		}
		JFreeChart barChart = ChartFactory.createBarChart("Monthly expenses by category", "Month", "Totals", monthTotalsDataset, PlotOrientation.VERTICAL, true, true, false);
		ChartPanel chartPanel = new ChartPanel(barChart);
		return chartPanel;
	}
}
