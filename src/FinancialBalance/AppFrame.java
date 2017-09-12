package FinancialBalance;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.lang.reflect.Field;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Jan Wilczek
 *
 * A class responsible for application's GUI layout.
 * Bases on box layout.
 */
public class AppFrame extends JFrame {
	
	public AppFrame(FinancialBalance financialBalance)
	{
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		int baseWidth = 700;
		int baseHeight = 400;
		this.setSize(baseWidth, baseHeight);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		
		this.setLocation(dim.width / 2 - baseWidth / 2, dim.height / 2 - baseHeight / 2);
		
		this.setTitle("Financial Balance");
		
		// Boxes:
		Box mainBox = Box.createHorizontalBox(); // Main box containing two sub-panels: reports panel and right panel (add panel above table)
		Box reportsBox = Box.createVerticalBox(); // Reports box
		Box rightBox = Box.createVerticalBox(); // Right box
		Box addBox = Box.createHorizontalBox();	// Add box
		Box tableBox = Box.createVerticalBox();	// Table box

		// reportsBox content:
		Object [][] reportsData = new Object [1][1];
		String [] reportHeader = {"Monthly reports"};
		JTable reportsTable = new JTable(new DefaultTableModel(reportsData,reportHeader));
		reportsBox.add(reportsTable);
		
		
		// addBox content:
		//Creating a text field for the expense name
		JTextField nameField = new JTextField("Insert the expense name here...                                 ");
		nameField.setBounds(0, 0, 100, 200);	// a line to reconsider
		addBox.add(nameField);
		
		//Creating a drop-down menu with the expense categories
		JComboBox<ExpenseCategory> categoryCombo = new JComboBox<ExpenseCategory>(ExpenseCategory.values());
		addBox.add(categoryCombo);
		
		//Creating a field to input date of the expense
		JFormattedTextField dateField = new JFormattedTextField();
		dateField.setValue(new Date());
		addBox.add(dateField);
		
		//Creating a field for price input
		JFormattedTextField priceField = new JFormattedTextField();
		priceField.setValue(new BigDecimal("0.00"));
		priceField.setColumns(4);
		addBox.add(priceField);
		
		//Creating an 'Add' button to add new expenses
		JButton addButton = new JButton("Add expense");
		addBox.add(addButton);
		
		
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
		JTable expensesTable = new JTable(new DefaultTableModel(expensesData,columnNames));		//TODO: Consider a new table model
		JScrollPane tableScrollPane = new JScrollPane(expensesTable);
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
		
		tableBox.add(Box.createHorizontalGlue());
		tableBox.add(tableScrollPane);
		
		
		//Box layout order
		rightBox.add(addBox);
		rightBox.add(tableBox);
		mainBox.add(reportsBox);
		mainBox.add(rightBox);
		
		
		this.add(mainBox);
		this.setVisible(true);
		
		//Inner listeners
		class AddButtonListener implements ActionListener
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				Expense expenseToAdd = new Expense(nameField.getText(), categoryCombo.getItemAt(0), (Date)dateField.getValue(), (BigDecimal)priceField.getValue());
				financialBalance.addExpense(expenseToAdd);
			}	
		}
		
		//Adding listeners
		addButton.addActionListener(new AddButtonListener());
		
		
	}
}
