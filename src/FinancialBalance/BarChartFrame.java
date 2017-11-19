package FinancialBalance;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.*;

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
 * @date 19.11.17
 * @version 1.0
 * 
 * A class responsible for drawing monthly expense statistics.
 */
public class BarChartFrame extends JFrame{
	private static final long serialVersionUID = 459847795940849256L;
	private Map<YearMonth, MonthlyReport> monthlyReports;
	private YearMonth month;
	private int baseWidth = 600;
	private int baseHeight = 500;
	private JFreeChart barChart;
	
	public BarChartFrame(Map<YearMonth, MonthlyReport> monthlyReports)
	{
		this.monthlyReports = monthlyReports;
		this.setTitle("Financial Balance Statistics");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(baseWidth, baseHeight);
		
		month = YearMonth.now();
		
		// Creating the chart
		barChart = ChartFactory.createBarChart("Monthly expenses by category", "Month", "Totals", createDataset(), PlotOrientation.VERTICAL, true, true, false);
		ChartPanel chartPanel = new ChartPanel(barChart);
		this.add(chartPanel);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		this.setLocation(dim.width / 4 - baseWidth / 2, dim.height / 4 - baseHeight / 2);
		this.setVisible(true);
	}
	
	private DefaultCategoryDataset createDataset()
	{
		DefaultCategoryDataset monthTotalsDataset = new DefaultCategoryDataset();
		ExpenseCategory[] categories = ExpenseCategory.values();
		MonthlyReport report = monthlyReports.get(month);
		for (ExpenseCategory category : categories)
			monthTotalsDataset.addValue(report.getCategoryTotal(category),month,category);
		return monthTotalsDataset;
	}
}
