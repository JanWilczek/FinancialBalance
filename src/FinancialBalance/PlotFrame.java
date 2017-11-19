package FinancialBalance;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.*;

import java.time.YearMonth;
import java.util.Map;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset; 

/**
 * 
 * @author Jan F. Wilczek
 * @date 17.11.17
 * @version 1.0
 * 
 * A class responsible for drawing expenses history plot.
 */
public class PlotFrame extends JFrame{
	private static final long serialVersionUID = -615419300303111875L;
	private Map<YearMonth, MonthlyReport> monthlyReports;
	private int baseWidth = 600;
	private int baseHeight = 500;
	private JFreeChart lineChart;
	
	public PlotFrame(Map<YearMonth, MonthlyReport> monthlyReports)
	{
		this.monthlyReports = monthlyReports;
		this.setTitle("Financial Balance Statistics");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(baseWidth, baseHeight);
		
		// Creating the chart
		lineChart = ChartFactory.createLineChart("Expenses history", "Months", "Totals", createDataset(), PlotOrientation.VERTICAL, true, true, false);
		ChartPanel chartPanel = new ChartPanel(lineChart);
		this.add(chartPanel);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		this.setLocation(dim.width / 2 - baseWidth / 2, dim.height / 2 - baseHeight / 2);
		this.setVisible(true);
	}
	
	private DefaultCategoryDataset createDataset()
	{
		DefaultCategoryDataset totalsDataset = new DefaultCategoryDataset();
		for (Map.Entry<YearMonth,MonthlyReport> entry : monthlyReports.entrySet())
			totalsDataset.addValue(entry.getValue().getTotal(), "Total", entry.getKey().toString());
		return totalsDataset;
	}
}
