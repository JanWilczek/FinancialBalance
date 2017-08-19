package FinancialBalance;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.lang.reflect.Field;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
		
		//Creating a panel
		JPanel addPanel = new JPanel();
		this.add(addPanel);
		
		//Creating a text field for the expense name
		JTextField nameField = new JTextField("Insert the expense name here...                                 ");
		nameField.setBounds(0, 0, 100, 200);	// a line to reconsider
		addPanel.add(nameField);
		
		//Creating a drop-down menu with the expense categories
		JComboBox<ExpenseCategory> categoryCombo = new JComboBox<ExpenseCategory>(ExpenseCategory.values());
		addPanel.add(categoryCombo);
		
		//Creating a field to input date of the expense
		JFormattedTextField dateField = new JFormattedTextField();
		dateField.setValue(new Date());
		addPanel.add(dateField);
		
		//Creating a field for price input
		JFormattedTextField priceField = new JFormattedTextField();
		priceField.setValue(new BigDecimal("0.00"));
		priceField.setColumns(4);
		addPanel.add(priceField);
		
		//Creating an 'Add' button to add new expenses
		JButton addButton = new JButton("Add expense");
		addPanel.add(addButton);
		
		//Set the table data
		//Get column names from Expense fields as a String array
		String[] columnNames = null;
		try {
			Class expenseClass = Class.forName("FinancialBalance.Expense");
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
		//expensesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //TODO: Beautify the table
		//JPanel tablePanel = new JPanel();	//TODO: Table box always on bottom
		//this.add(tablePanel);
		addPanel.add(tableScrollPane);
		
		
		
		
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
