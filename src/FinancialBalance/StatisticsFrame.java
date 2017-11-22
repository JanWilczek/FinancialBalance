package FinancialBalance;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.time.YearMonth;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

/**
 * @author Jan F. Wilczek
 * @date 20.11.17
 * 
 * @version 1.0
 *
 * A class responsible for displaying expenses' statistics in a separate window.
 * 
 */
public class StatisticsFrame extends JFrame{
	private static final long serialVersionUID = 4893816996478741925L;
	private int baseWidth = 650;
	private int baseHeight = 550;
	private JTabbedPane tabbedPane;
	private JCheckBox totals;
	private JCheckBox[] categoriesTotals;
	private JComboBox<YearMonth> monthCombo;
	
	/**
	 * Public constructor.
	 * @param monthlyReports
	 */
	public StatisticsFrame(Map<YearMonth, MonthlyReport> monthlyReports) {
		this.setTitle("Expense Statistics");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(baseWidth, baseHeight);
		
		// TODO: Implement GridBagLayout in each of the panels
		
		JPanel totalsHistoryPanel = new JPanel();
		
		JPanel categoriesPanel = new JPanel();
		
		totals = new JCheckBox("Totals", true);
		categoriesPanel.add(totals);
		ExpenseCategory[] categories = ExpenseCategory.values();
		categoriesTotals = new JCheckBox[categories.length];
		for (int i = 0 ; i < categories.length; i++){
			categoriesTotals[i] = new JCheckBox(categories[i].toString());
			categoriesPanel.add(categoriesTotals[i]);
		}		
		
		totalsHistoryPanel.add(categoriesPanel);
		totalsHistoryPanel.add(StatisticsChartPanelFactory.createLinePlotPanel(monthlyReports));
		
		JPanel expensesPerCategoryPanel = new JPanel();
		
		YearMonth[] monthsInReports = new YearMonth[monthlyReports.keySet().size()];
		int i = 0;
		for (YearMonth month : monthlyReports.keySet())
		{
			monthsInReports[i] = month;
			i++;
		}
		monthCombo = new JComboBox<YearMonth>(monthsInReports);
		
		expensesPerCategoryPanel.add(monthCombo);
		expensesPerCategoryPanel.add(StatisticsChartPanelFactory.createBarChartPanel(monthlyReports, YearMonth.now()));
		
		tabbedPane = new JTabbedPane();
		tabbedPane.add("Totals history", totalsHistoryPanel);
		tabbedPane.add("Expenses per category",expensesPerCategoryPanel);
		
		this.add(tabbedPane);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		this.setLocation(dim.width / 3 - baseWidth / 2, dim.height / 3 - baseHeight / 2);
		this.setVisible(true);
	}

}
