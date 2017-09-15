package FinancialBalance;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.lang.reflect.Field;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
		int baseWidth = 700;
		int baseHeight = 600;
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
		reportsPanel = new JPanel();
		//rightPanel = new JPanel();
		//rightPanel.setLayout(new GridBagLayout());
		addPanel = new JPanel();
		Border addBorder = BorderFactory.createTitledBorder("Add an expense");
		addPanel.setBorder(addBorder);
		//tablePanel = new JPanel();
		
		// Setting up the layouts
		mainLayoutConstraints = new GridBagConstraints();
		setLayoutConstraints(1,1,1,2,1,1);
		mainLayoutConstraints.insets = new Insets(4,4,4,4);


		// reportsBox content:
		Object [][] reportsData = new Object [1][1];
		String [] reportHeader = {"Monthly reports"};
		reportsTable = new JTable(new DefaultTableModel(reportsData,reportHeader));
		reportsPanel.add(reportsTable);
		mainPanel.add(reportsPanel, mainLayoutConstraints);
		
		
		// addBox content:
		//Creating a text field for the expense name
		nameField = new JTextField(defaultName);
		nameField.setBounds(0, 0, 100, 200);	// a line to reconsider
		nameField.addFocusListener(new PriceFieldListener());
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
		priceField.setValue("0,00");
		priceField.setColumns(4);
		priceField.addFocusListener(new PriceFieldListener());
		addPanel.add(priceField);
		
		//Creating an 'Add' button to add new expenses
		addButton = new JButton("Add expense");
		addButton.addActionListener(new AddButtonListener());
		addPanel.add(addButton);
		
		setLayoutConstraints(1,1,1,1,3,1);
		mainPanel.add(addPanel,mainLayoutConstraints);
		
		// tableBox content
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
					expensesData[i][2] = expense.getDate();
					expensesData[i][3] = expense.getPrice();
					i++;
				}
				
			}
		
		//Create the table
		//JTable expensesTable = new JTable(new ExpenseTableModel(expensesData,columnNames));
		expensesTable = new JTable(new DefaultTableModel(expensesData,columnNames));		//TODO: Consider a new table model
		tableScrollPane = new JScrollPane(expensesTable);
		expensesTable.setFillsViewportHeight(true);
		expensesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //TODO: Beautify the table
		
		// Setting up columns' width
		TableColumn namesColumn = expensesTable.getColumnModel().getColumn(0);
		//namesColumn.setPreferredWidth(nameField.getWidth());
		namesColumn.setPreferredWidth(380);
		
		TableColumn categoryColumn = expensesTable.getColumnModel().getColumn(1);
		categoryColumn.setPreferredWidth(60);
		
		TableColumn dateColumn = expensesTable.getColumnModel().getColumn(2);
		dateColumn.setPreferredWidth(100);
		
		TableColumn priceColumn = expensesTable.getColumnModel().getColumn(3);
		priceColumn.setPreferredWidth(50);
		
		//tablePanel.add(Box.createHorizontalGlue());
		//tablePanel.add(tableScrollPane);
		setLayoutConstraints(1,2,1,1,3,10);
		mainPanel.add(tableScrollPane, mainLayoutConstraints);
		
		
		//Box layout order
		//rightPanel.add(addPanel);
		//rightPanel.add(tablePanel);
		//mainPanel.add(reportsPanel);
		//mainPanel.add(rightPanel);
		
		
		this.add(mainPanel);
		this.setResizable(false);
		this.setVisible(true);
			
	}
	
	private void setLayoutConstraints(int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty) 
	{
		if (gridx != 0 ) mainLayoutConstraints.gridx = gridx;
		if (gridy != 0 ) mainLayoutConstraints.gridy = gridy;
		if (gridwidth != 0 ) mainLayoutConstraints.gridwidth = gridwidth;
		if (gridwidth != 0 ) mainLayoutConstraints.gridheight = gridheight;
		if (weightx != 0 ) mainLayoutConstraints.weightx = weightx;
		if (weighty != 0 ) mainLayoutConstraints.weighty = weighty;
	}
	
	//Inner listeners
	class AddButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().equals(addButton)) {
				// Check for date correctness
				Date expenseDate = null;
				try {
					expenseDate = (Date) dateField.getValue();
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
				//nameField.setText(defaultName);
				//dateField.setValue(new Date());
				//priceField.setValue("0.00");
			}
		}	
	}
	
	class PriceFieldListener implements FocusListener
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
				if (nameField.getText().equals("0.00"))
				{
					nameField.setText("");
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
				if (nameField.getText().equals(""))
				{
					nameField.setText("0.00");
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
	private JPanel rightPanel;
	private JPanel addPanel;
	private JPanel tablePanel;
	
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
	
	// layout constraints
	private GridBagConstraints mainLayoutConstraints;
	
	// other private members
	private String defaultName = "Insert the expense name here...                                 ";
}
