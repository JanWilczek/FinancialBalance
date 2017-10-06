package FinancialBalance;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.lang.reflect.Field;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Jan Wilczek
 * @date 15.09.17
 * 
 * @version 1.0
 * A class responsible for application's GUI layout.
 * Bases on box layout.
 */
public class AppFrame extends JFrame {
	

	public AppFrame(FinancialBalance financialBalance)
	{
		this.financialBalance = financialBalance;
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setSize(baseWidth, baseHeight);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		
		this.setLocation(dim.width / 2 - baseWidth / 2, dim.height / 2 - baseHeight / 2);
		
		this.setTitle("Financial Balance");
		
		// Boxes:
//		Box mainBox = Box.createHorizontalBox(); // Main box containing two sub-panels: reports panel and right panel (add panel above table)
//		Box reportsBox = Box.createVerticalBox(); // Reports box
//		Box rightBox = Box.createVerticalBox(); // Right box
//		Box addBox = Box.createHorizontalBox();	// Add box
//		Box tableBox = Box.createVerticalBox();	// Table box
		
		// Panels:
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		//reportsPanel = new JPanel();
		//rightPanel = new JPanel();
		//rightPanel.setLayout(new GridBagLayout());
		addPanel = new JPanel();
		Border addBorder = BorderFactory.createTitledBorder("Add an expense");
		addPanel.setBorder(addBorder);
		//tablePanel = new JPanel();
		
		// Setting up the layouts
		mainLayoutConstraints = new GridBagConstraints();
		mainLayoutConstraints.insets = new Insets(2,2,2,2);

		// reportsBox content:
		generateReportsTable();
		
		
		// addBox content:
		//Creating a text field for the expense name
		nameField = new JTextField(defaultName);
		//nameField.setBounds(0, 0, 100, 200);	// a line to reconsider
		nameField.addFocusListener(new NameAndPriceFieldListener());
		addPanel.add(nameField);
		
		//Creating a drop-down menu with the expense categories
		categoryCombo = new JComboBox<ExpenseCategory>(ExpenseCategory.values());
		addPanel.add(categoryCombo);
		
		//Creating a field to input date of the expense
		dateField = new JSpinner(new SpinnerDateModel(new Date(), null, new Date(), Calendar.DAY_OF_MONTH));
		JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateField,  "dd-MM-yy");
		dateField.setEditor(dateEditor);
		dateField.setSize(6, nameField.getHeight());	// TODO: doesn't work
		addPanel.add(dateField);
		
		//Creating a field for price input
		priceField = new JFormattedTextField();
		priceField.setValue("0.00");
		priceField.setColumns(4);
		priceField.addFocusListener(new NameAndPriceFieldListener());
		addPanel.add(priceField);
		
		//Creating an 'Add' button to add new expenses
		addButton = new JButton("Add expense");
		addButton.addActionListener(new AddButtonListener());
		addPanel.add(addButton);
		
		setLayoutConstraints(1,0,1,1,0.7,0);
		//mainLayoutConstraints.ipadx = (int) 0.7 * baseWidth;
		//mainLayoutConstraints.ipady = (int) 0.2 * baseHeight;
		mainLayoutConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		mainLayoutConstraints.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(addPanel,mainLayoutConstraints);
		
		// tableBox content
		generateExpensesTable();		
		
		//Box layout order
		//rightPanel.add(addPanel);
		//rightPanel.add(tablePanel);
		//mainPanel.add(reportsPanel);
		//mainPanel.add(rightPanel);
		
		
		this.add(mainPanel);
		this.setResizable(true);
		this.setVisible(true);
			
	}

	private void generateReportsTable() {
		Object [][] reportsData = new Object [financialBalance.getMonthlyReports().size()][2];
		int dataIndex = 0;
		for (Map.Entry<YearMonth, MonthlyReport> monthlyReport : financialBalance.getMonthlyReports().entrySet())
		{
			reportsData[dataIndex][0] = monthlyReport.getKey().format(DateTimeFormatter.ofPattern("yyyy-MM"));
			reportsData[dataIndex][1] = monthlyReport.getValue().getTotal();
			dataIndex++;
		}
		String [] reportHeader = {"Month", "Total"};
		reportsTable = new JTable(new DefaultTableModel(reportsData,reportHeader));
		//reportsPanel.add(reportsTable);
		reportsScrollPane = new JScrollPane(reportsTable);
		reportsTable.setFillsViewportHeight(true);
		//reportsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //TODO: Beautify the table
		setLayoutConstraints(0,0,1,2,0.3,1);
		//mainLayoutConstraints.ipadx = (int) 0.3 * baseWidth;
		//mainLayoutConstraints.ipady = baseHeight;
		mainLayoutConstraints.ipadx = 0;
		//mainLayoutConstraints.ipady = 0;
		mainLayoutConstraints.anchor = GridBagConstraints.PAGE_START;
		mainLayoutConstraints.fill = GridBagConstraints.BOTH;
		mainPanel.add(reportsScrollPane, mainLayoutConstraints);
	}

	private void generateExpensesTable() {
		//if (tableScrollPane != null) mainPanel.remove(tableScrollPane);
		//Set the table data
		//Get column names from Expense fields as a String array
		String[] columnNames = null;
		try {
			Class<?> expenseClass = Class.forName("FinancialBalance.Expense");
			Field[] expenseClassFields = expenseClass.getDeclaredFields();
			columnNames = new String[expenseClassFields.length];
			for (int i = 0; i<expenseClassFields.length ; i++)
			{
				columnNames[i] = expenseClassFields[i].getName();
			}
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (SecurityException se){
			se.printStackTrace();
		}
		
		//Get the expense data as a two-dimensional Object array
		Object[][] expensesData = null;
		if (columnNames != null) 
			{
				List<Expense> expensesList = financialBalance.getExpenses();
				expensesData = new Object[expensesList.size()][columnNames.length];
				int i = 0;
				for (Expense expense : expensesList)
				{
					expensesData[i][0] = expense.getName();
					expensesData[i][1] = expense.getCategory();
					expensesData[i][2] = DateFormat.getDateInstance().format(expense.getDate().getTime());
					expensesData[i][3] = expense.getPrice();
					i++;
				}
				
			}
		
		//Create the table
		//JTable expensesTable = new JTable(new ExpenseTableModel(expensesData,columnNames));
		expensesTable = new JTable(new DefaultTableModel(expensesData,columnNames));		//TODO: Consider a new table model
		tableScrollPane = new JScrollPane(expensesTable);
		expensesTable.setFillsViewportHeight(true);
		expensesTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS); //TODO: Beautify the table
		
		// Setting up columns' width
		TableColumn namesColumn = expensesTable.getColumnModel().getColumn(0);
		//namesColumn.setPreferredWidth(nameField.getWidth());
		namesColumn.setPreferredWidth(400);
		/*
		TableColumn categoryColumn = expensesTable.getColumnModel().getColumn(1);
		categoryColumn.setPreferredWidth(50);
		
		TableColumn dateColumn = expensesTable.getColumnModel().getColumn(2);
		dateColumn.setPreferredWidth(30);
		
		TableColumn priceColumn = expensesTable.getColumnModel().getColumn(3);
		priceColumn.setPreferredWidth(40);*/
		//expensesTable.do
		
		//tablePanel.add(Box.createHorizontalGlue());
		//tablePanel.add(tableScrollPane);
		setLayoutConstraints(1,1,1,1,0.7,0.8);
		mainLayoutConstraints.ipadx = (int) 0.7 * baseWidth;
		//mainLayoutConstraints.ipady = (int) 0.8 * baseHeight;
		//mainLayoutConstraints.ipady = expensesTable.getHeight();
		mainLayoutConstraints.anchor = GridBagConstraints.PAGE_START;
		mainLayoutConstraints.fill = GridBagConstraints.BOTH;
		mainPanel.add(tableScrollPane, mainLayoutConstraints);
	}
	
	private void setLayoutConstraints(int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty) 
	{
		mainLayoutConstraints.gridx = gridx;
		mainLayoutConstraints.gridy = gridy;
		mainLayoutConstraints.gridwidth = gridwidth;
		mainLayoutConstraints.gridheight = gridheight;
		mainLayoutConstraints.weightx = weightx;
		mainLayoutConstraints.weighty = weighty;
	}
	
	//Inner listeners
	class AddButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().equals(addButton)) {
				// Check for date correctness
				Calendar expenseDate = Calendar.getInstance();
				try {
					expenseDate.setTime((Date) dateField.getValue());
				} catch (Exception e) {
					JOptionPane.showMessageDialog(AppFrame.this, "Incorrect date format!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				// Check for price correctness
				BigDecimal expensePrice = null;
				try {
					expensePrice = new BigDecimal(priceField.getValue().toString());
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(AppFrame.this, "Incorrect price format!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				Expense expenseToAdd = new Expense(nameField.getText(),
						(ExpenseCategory) categoryCombo.getSelectedItem(), expenseDate, expensePrice);
				financialBalance.addExpense(expenseToAdd);
				//expensesTable.getModel().fireTableDataChanged();
				//nameField.setText(defaultName);
				//dateField.setValue(new Date());
				//priceField.setValue("0.00");
			}
		}	
	}
	
	class NameAndPriceFieldListener implements FocusListener
	{

		@Override
		public void focusGained(FocusEvent fe) {
			if (fe.getSource().equals(nameField))
			{
				if (nameField.getText().equals(defaultName))
				{
					nameField.setText("");
				}
			}
			else if (fe.getSource().equals(priceField))
			{
				if (priceField.getText().equals("0.00"))
				{
					priceField.setText("");
				}
			}
		}
		
		@Override
		public void focusLost(FocusEvent fe)
		{
			if (fe.getSource().equals(nameField))
			{
				if (nameField.getText().length() == 0)
				{
					nameField.setText(defaultName);
				}
			}
			else if (fe.getSource().equals(priceField))
			{
				if (priceField.getText().equals(""))
				{
					priceField.setText("0.00");
				}
			}
		}
		
	}
	
	// private members
	// inner application logic
	private FinancialBalance financialBalance;
	
	// GUI elements
	// panels
	private JPanel mainPanel;
	private JPanel reportsPanel;
	//private JPanel rightPanel;
	private JPanel addPanel;
	//private JPanel tablePanel;
	
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
	private JScrollPane tableScrollPane;
	private JScrollPane reportsScrollPane;
	
	// layout constraints
	private GridBagConstraints mainLayoutConstraints;
	
	// other private members
	private String defaultName = "Insert the expense name here...                                 ";
	private int baseWidth = 850;
	private int baseHeight = 400;
	
	// required by JFrame
	private static final long serialVersionUID = 6750128568375064611L;
}
