package FinancialBalance;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.time.YearMonth;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

public class StatisticsFrame extends JFrame{
	private static final long serialVersionUID = 4893816996478741925L;
	private int baseWidth = 600;
	private int baseHeight = 500;
	private JTabbedPane tabbedPane;
	
	public StatisticsFrame(Map<YearMonth, MonthlyReport> monthlyReports) {
		this.setTitle("Expense Statistics");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(baseWidth, baseHeight);
		
		tabbedPane = new JTabbedPane();
		tabbedPane.add("Name1", new BarChartFrame(monthlyReports));
		tabbedPane.add("Name2", new PlotFrame(monthlyReports));
		
		this.add(tabbedPane);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		this.setLocation(dim.width / 3 - baseWidth / 2, dim.height / 3 - baseHeight / 2);
		this.setVisible(true);
	}

}
