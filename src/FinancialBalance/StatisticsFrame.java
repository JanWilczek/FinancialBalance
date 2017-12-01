package FinancialBalance;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.YearMonth;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;

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
	private JCheckBox totalsCheckBox;
	private JCheckBox[] categoriesTotalsCheckBoxes;
	private JComboBox<YearMonth> monthCombo;
	
	private JTabbedPane tabbedPane;
	private JPanel totalsHistoryPanel;
	private JPanel categoriesPanel;
	private ChartPanel plotChartPanel;
	private JPanel expensesPerCategoryPanel;
	private ChartPanel barChartPanel;
	
	private Map<YearMonth, MonthlyReport> monthlyReports;
	
	/**
	 * Public constructor.
	 * @param monthlyReports
	 */
	public StatisticsFrame(Map<YearMonth, MonthlyReport> monthlyReports) {
		this.monthlyReports = monthlyReports;
		
		this.setTitle("Expense Statistics");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(baseWidth, baseHeight);
		
		// TODO: Implement GridBagLayout in each of the panels
		
		totalsHistoryPanel = new JPanel();
		
		categoriesPanel = new JPanel();
		
		totalsCheckBox = new JCheckBox("Totals", true);
		totalsCheckBox.addChangeListener(checkBoxListener);
		categoriesPanel.add(totalsCheckBox);
		
		ExpenseCategory[] categories = ExpenseCategory.values();
		categoriesTotalsCheckBoxes = new JCheckBox[categories.length];
		for (int i = 0 ; i < categories.length; i++){
			categoriesTotalsCheckBoxes[i] = new JCheckBox(categories[i].toString());
			categoriesTotalsCheckBoxes[i].addChangeListener(checkBoxListener);
			categoriesPanel.add(categoriesTotalsCheckBoxes[i]);
			//totalsHistoryPanel.add(categoriesTotals[i]);	// The result is the same as of the line above
		}
		
		totalsHistoryPanel.add(categoriesPanel);
		updatePlotChart();
		
		expensesPerCategoryPanel = new JPanel();
		
		List<YearMonth> monthsToDisplay = new LinkedList<>(monthlyReports.keySet());
		Collections.reverse(monthsToDisplay);
		monthCombo = new JComboBox<YearMonth>(monthsToDisplay.toArray(new YearMonth[monthsToDisplay.size()]));
		monthCombo.addActionListener(comboBoxListener);
		
		expensesPerCategoryPanel.add(monthCombo);
		updateBarChart();
		
		tabbedPane = new JTabbedPane();
		tabbedPane.add("Totals history", totalsHistoryPanel);
		tabbedPane.add("Expenses per category",expensesPerCategoryPanel);
		
		this.add(tabbedPane);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		this.setLocation(dim.width / 3 - baseWidth / 2, dim.height / 3 - baseHeight / 2);
		this.setVisible(true);
	}
	
	private void updatePlotChart()
	{
		if (plotChartPanel != null) totalsHistoryPanel.remove(plotChartPanel);
		List<ExpenseCategory> displayedCategories = new LinkedList<>();
		for (JCheckBox categoryCheckBox : categoriesTotalsCheckBoxes)
			if (categoryCheckBox.isSelected())
				displayedCategories.add(ExpenseCategory.valueOf(categoryCheckBox.getText()));
		plotChartPanel = StatisticsChartPanelFactory.createLinePlotPanel(
				monthlyReports, 
				totalsCheckBox.isSelected(),
				displayedCategories.toArray(new ExpenseCategory[displayedCategories.size()])
				);
		totalsHistoryPanel.add(plotChartPanel);
		totalsHistoryPanel.validate();
	}
	
	private void updateBarChart()
	{
		if (monthCombo.getItemCount() > 0) {
			if (barChartPanel != null) expensesPerCategoryPanel.remove(barChartPanel);
			barChartPanel = StatisticsChartPanelFactory.createBarChartPanel(monthlyReports, (YearMonth) monthCombo.getSelectedItem());
			expensesPerCategoryPanel.add(barChartPanel);
			if (this.isVisible()) expensesPerCategoryPanel.validate();
		}
	}
	
	private ActionListener comboBoxListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().equals(monthCombo)) updateBarChart();
		}
	};
	
	private ChangeListener checkBoxListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent ce) {
			updatePlotChart();
		}	
	};
	
	
}
