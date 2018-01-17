package FinancialBalance;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author Jan Wilczek
 * @date 15.09.17
 * 
 * @version 1.0
 * A class responsible for application's GUI layout.
 * Bases on GridBagLayout.
 */
public class FinancialBalanceView extends JFrame {
	
	/**
	 * Public constructor.
	 * @param financialBalance
	 */
	public FinancialBalanceView()
	{
		this.setSize(baseWidth, baseHeight);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		
		this.setLocation(dim.width / 2 - baseWidth / 2, dim.height / 2 - baseHeight / 2);
		
		this.setTitle("Financial Balance");
		
		// Panels:
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		addPanel = new JPanel();
		Border addBorder = BorderFactory.createTitledBorder("Add an expense");
		addPanel.setBorder(addBorder);
		
		// Setting up the layouts
		mainLayoutConstraints = new GridBagConstraints();
		mainLayoutConstraints.insets = new Insets(2,2,2,2);

		// addPanel content:
		//Creating a text field for the expense name
		setNameField(new JTextField(getDefaultExpenseName()));
		getNameField().addFocusListener(new NameAndPriceFieldListener());
		addPanel.add(getNameField());
		
		//Creating a drop-down menu with the expense categories
		setCategoryCombo(new JComboBox<ExpenseCategory>(ExpenseCategory.values()));
		addPanel.add(getCategoryCombo());
		
		//Creating a field to input date of the expense
		setDateField(new JSpinner(new SpinnerDateModel(new Date(), null, new Date(), Calendar.DAY_OF_MONTH)));
		JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(getDateField(),  "dd-MM-yy");
		getDateField().setEditor(dateEditor);
		getDateField().setSize(6, getNameField().getHeight());	// TODO: doesn't work
		addPanel.add(getDateField());
		
		//Creating a field for price input
		setPriceField(new JFormattedTextField());
		getPriceField().setValue("0.00");
		getPriceField().setColumns(4);
		getPriceField().addFocusListener(new NameAndPriceFieldListener());
		addPanel.add(getPriceField());
		
		//Creating an 'Add' button to add new expenses
		addButton = new JButton("Add expense");
		addPanel.add(addButton);
		
		setLayoutConstraints(1,0,1,1,0.7,0);	// Second column, first row, 1x1 cell large, 70% of the GUI's width and let Java decide how much height
		mainLayoutConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		mainLayoutConstraints.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(addPanel,mainLayoutConstraints);
		
		// tableScrollPane content
		expensesTable = new JTable();
		expensesScrollPane = new JScrollPane(expensesTable);
		expensesTable.setFillsViewportHeight(true);
		expensesTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		setLayoutConstraints(1,1,1,1,0.7,0.8);
		mainLayoutConstraints.anchor = GridBagConstraints.PAGE_START;
		mainLayoutConstraints.fill = GridBagConstraints.BOTH;		// Fill both horizontally and vertically the tableScrollPane.
		mainPanel.add(expensesScrollPane, mainLayoutConstraints);
		
		// reportsScrollPane content:
		reportsTable =  new JTable();
		reportsScrollPane = new JScrollPane(reportsTable);
		reportsTable.setFillsViewportHeight(true);	// Fill the remaining height of the viewport.
		setLayoutConstraints(0,0,1,2,0.2,1);	// First row, first column, 1x2 vertical cells size, 20% of the GUI's width, all of the column's height.
		mainLayoutConstraints.ipadx = 0;	// Reset to default value.
		mainLayoutConstraints.anchor = GridBagConstraints.PAGE_START;
		mainLayoutConstraints.fill = GridBagConstraints.BOTH;	// Fill both horizontally and vertically the reportsScrollPane.
		mainPanel.add(reportsScrollPane, mainLayoutConstraints);	
		
		// Create menu
		menuBar = new JMenuBar();
		
		viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		viewMenu.getAccessibleContext().setAccessibleDescription("View additional windows.");
		menuBar.add(viewMenu);
		
		statisticsMenuItem = new JMenuItem("Statistics", KeyEvent.VK_S);
		statisticsMenuItem.getAccessibleContext().setAccessibleDescription("Show statistics of all expenses.");
		viewMenu.add(statisticsMenuItem);
		
		this.setJMenuBar(menuBar);
		
		try {
			this.setIconImage(ImageIO.read(new File("img/icon.png")));
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		
		this.add(mainPanel);
		this.setResizable(true);			
	}
	
	/**
	 * 	Helper method changing layout constraints for proper GUI elements' placement.
	 */
	private void setLayoutConstraints(int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty) 
	{
		mainLayoutConstraints.gridx = gridx;
		mainLayoutConstraints.gridy = gridy;
		mainLayoutConstraints.gridwidth = gridwidth;
		mainLayoutConstraints.gridheight = gridheight;
		mainLayoutConstraints.weightx = weightx;
		mainLayoutConstraints.weighty = weighty;
	}
	
	// Inner listeners
	
	// private members
	// a helper inner class responsible for user-friendly text box interaction
	private class NameAndPriceFieldListener implements FocusListener
	{

		@Override
		public void focusGained(FocusEvent fe) {
			if (fe.getSource().equals(getNameField()))
			{
				if (getNameField().getText().equals(getDefaultExpenseName()))
				{
					getNameField().setText("");
				}
			}
			else if (fe.getSource().equals(getPriceField()))
			{
				if (getPriceField().getText().equals("0.00"))
				{
					getPriceField().setText("");
				}
			}
		}
		
		@Override
		public void focusLost(FocusEvent fe)
		{
			if (fe.getSource().equals(getNameField()))
			{
				if (getNameField().getText().length() == 0)
				{
					getNameField().setText(getDefaultExpenseName());
				}
			}
			else if (fe.getSource().equals(getPriceField()))
			{
				if (getPriceField().getText().equals(""))
				{
					getPriceField().setText("0.00");
				}
			}
		}
		
	}
		
	// public getters
	public JTable getExpensesTable() { return this.expensesTable; }
	public void setExpensesTable(JTable expensesTable) { this.expensesTable = expensesTable; } 
	public JTable getReportsTable() { return reportsTable;	}
	public void setReportsTable(JTable reportsTable) { this.reportsTable = reportsTable; }
	public JScrollPane getExpensesScrollPane() {	return expensesScrollPane;	}
	public void setExpensesScrollPane(JScrollPane expensesScrollPane) { this.expensesScrollPane = expensesScrollPane; }
	public JScrollPane getReportsScrollPane() {	return reportsScrollPane;	}
	public void setReportsScrollPane(JScrollPane reportsScrollPane) {	this.reportsScrollPane = reportsScrollPane;	}
	public JButton getAddButton() { return this.addButton; }
	public JTextField getNameField() {	return nameField;	}
	public void setNameField(JTextField nameField) {this.nameField = nameField;	}
	public JComboBox<ExpenseCategory> getCategoryCombo() {	return categoryCombo; }
	public void setCategoryCombo(JComboBox<ExpenseCategory> categoryCombo) {this.categoryCombo = categoryCombo;	}
	public JSpinner getDateField() {return dateField;}
	public void setDateField(JSpinner dateField) {this.dateField = dateField;}
	public JFormattedTextField getPriceField() {return priceField;	}
	public void setPriceField(JFormattedTextField priceField) {	this.priceField = priceField;}
	public DateFormat getDateFormat() {	return dateFormat;}
	public void setDateFormat(DateFormat dateFormat) {this.dateFormat = dateFormat;}
	public String getDefaultExpenseName() {return defaulExpensetName;	}
	public void setDefaultExpenseName(String defaultName) {this.defaulExpensetName = defaultName;	}
	public JPanel getMainPanel() {	return this.mainPanel; }
	public JPanel getAddPanel() { return addPanel;}
	public JMenuItem getStatisticsMenuItem() { return statisticsMenuItem;}
	
	// GUI elements
	// panels
	private JPanel mainPanel;
	private JPanel addPanel;
	
	// scroll panes
	private JScrollPane expensesScrollPane;
	private JScrollPane reportsScrollPane;
	
	// buttons
	private JButton addButton;
	
	// tables
	private JTable expensesTable;
	private JTable reportsTable;
	
	// other fields
	private JTextField nameField;
	private JComboBox<ExpenseCategory> categoryCombo;
	private JSpinner dateField;
	private JFormattedTextField priceField;
	
	// Menu components
	private JMenuBar menuBar;
	private JMenu viewMenu;
	private JMenuItem statisticsMenuItem;
	
	// layout constraints used to adjust every GridBagLayout element
	private GridBagConstraints mainLayoutConstraints;
	
	// date format
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	
	// other private members
	private String defaulExpensetName = "Insert the expense name here...               ";
	private int baseWidth = 850;
	private int baseHeight = 500;
	
	// required by JFrame
	private static final long serialVersionUID = 6750128568375064611L;
}
