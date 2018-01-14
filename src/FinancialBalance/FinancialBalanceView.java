package FinancialBalance;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.lang.reflect.Field;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import FinancialBalance.threading.PlotFrameRunner;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	public FinancialBalanceView(FinancialBalance financialBalance)
	{
		this.financialBalance = financialBalance;
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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

		// reportsScrollPane content:
		generateReportsTable();
		
		// addPanel content:
		//Creating a text field for the expense name
		nameField = new JTextField(defaultName);
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
		
		setLayoutConstraints(1,0,1,1,0.7,0);	// Second column, first row, 1x1 cell large, 70% of the GUI's width and let Java decide how much height
		mainLayoutConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		mainLayoutConstraints.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(addPanel,mainLayoutConstraints);
		
		// tableScrollPane content
		generateExpensesTable();
		expensesTable.addKeyListener(new DeletePressedListener());
		
		// Create menu
		menuBar = new JMenuBar();
		
		viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		viewMenu.getAccessibleContext().setAccessibleDescription("View additional windows.");
		menuBar.add(viewMenu);
		
		statisticsMenuItem = new JMenuItem("Statistics", KeyEvent.VK_S);
		statisticsMenuItem.getAccessibleContext().setAccessibleDescription("Show statistics of all expenses.");
		statisticsMenuItem.addActionListener(new StatisticsMenuListener());
		viewMenu.add(statisticsMenuItem);
		
		this.setJMenuBar(menuBar);
		
		try {
			this.setIconImage(ImageIO.read(new File("img/icon.png")));
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		
		this.add(mainPanel);
		this.addWindowListener(new WindowClosingListener());
		this.setResizable(true);
		this.setVisible(true);
			
	}

	/**
	 * Generate the reports table.
	 */
	private void generateReportsTable() {
		Object [][] reportsData = new Object [financialBalance.getMonthlyReports().size()][2];
		int dataIndex = 0;
		List<Map.Entry<YearMonth, MonthlyReport>> monthlyReports = new LinkedList<>(financialBalance.getMonthlyReports().entrySet());
		Collections.reverse(monthlyReports);
		for (Map.Entry<YearMonth, MonthlyReport> monthlyReport : monthlyReports)
		{
			reportsData[dataIndex][0] = monthlyReport.getKey().format(DateTimeFormatter.ofPattern("yyyy-MM"));
			reportsData[dataIndex][1] = monthlyReport.getValue().getTotal();
			dataIndex++;
		}
		String [] reportHeader = {"Month", "Total"};
		reportsTable = new JTable(new DefaultTableModel(reportsData,reportHeader));
		reportsScrollPane = new JScrollPane(reportsTable);
		reportsTable.setFillsViewportHeight(true);	// Fill the remaining height of the viewport.
		setLayoutConstraints(0,0,1,2,0.2,1);	// First row, first column, 1x2 vertical cells size, 20% of the GUI's width, all of the column's height.
		mainLayoutConstraints.ipadx = 0;	// Reset to default value.
		mainLayoutConstraints.anchor = GridBagConstraints.PAGE_START;
		mainLayoutConstraints.fill = GridBagConstraints.BOTH;	// Fill both horizontally and vertically the reportsScrollPane.
		mainPanel.add(reportsScrollPane, mainLayoutConstraints);
	}
	
	/**
	 * Generate the expenses table.
	 */
	private void generateExpensesTable() {
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
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
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
					expensesData[i][2] = simpleDateFormat.format(expense.getDate().getTime());
					expensesData[i][3] = expense.getPrice();
					i++;
				}
				
			}
		
		//Create the table
		expensesTable = new JTable(new DefaultTableModel(expensesData,columnNames));
		tableScrollPane = new JScrollPane(expensesTable);
		expensesTable.setFillsViewportHeight(true);
		expensesTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		
		// Setting up columns' width
		TableColumn namesColumn = expensesTable.getColumnModel().getColumn(0);
		namesColumn.setPreferredWidth(400);	// Set the name field wide and with JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS make the next columns resized.
		
		setLayoutConstraints(1,1,1,1,0.7,0.8);
		mainLayoutConstraints.anchor = GridBagConstraints.PAGE_START;
		mainLayoutConstraints.fill = GridBagConstraints.BOTH;		// Fill both horizontally and vertically the tableScrollPane.
		mainPanel.add(tableScrollPane, mainLayoutConstraints);
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
	
	private void addEnteredExpense() {
		// Check for date correctness
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Calendar expenseDate = Calendar.getInstance();
		try {
			expenseDate.setTime(simpleDateFormat.parse(simpleDateFormat.format((Date) dateField.getValue())));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(FinancialBalanceView.this, "Incorrect date format!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Check for price correctness
		BigDecimal expensePrice = null;
		try {
			expensePrice = new BigDecimal(priceField.getValue().toString());
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(FinancialBalanceView.this, "Incorrect price format!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		Expense expenseToAdd = new Expense(nameField.getText(),
				(ExpenseCategory) categoryCombo.getSelectedItem(), expenseDate, expensePrice);
		/*Expense expenseToAdd = null;
		try {
			expenseToAdd = Expense.parseExpense(nameField.getText(), categoryCombo.getSelectedItem().toString(), dateField.getValue().toString(), priceField.getValue().toString(), "yyyy/MM/dd");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

		*/
		int index = financialBalance.addExpense(expenseToAdd);	// Add the expense to the main logic object.
		DefaultTableModel model = (DefaultTableModel) expensesTable.getModel();
		model.insertRow(index, new Object[] {expenseToAdd.getName(), expenseToAdd.getCategory(), simpleDateFormat.format(expenseToAdd.getDate().getTime()), expenseToAdd.getPrice()});

		// Reset nameField and priceField to their defaults. Leave the category and the date in case user wanted to add several objects with the same category or date.
		nameField.setText(defaultName);
		priceField.setValue("0.00");
	}

	private void deleteSelectedExpenses() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		int[] selectedRows = expensesTable.getSelectedRows();
		int[] rowsToDelete = new int[selectedRows.length];
		int j = 0;
		for (int i : selectedRows) {
			Calendar cal = Calendar.getInstance();
			try{cal.setTime(sdf.parse((String)expensesTable.getValueAt(i, 2)));}
			catch(ParseException pe){System.err.println(pe.getMessage());; return;	}
			Expense expenseToDelete = new Expense((String) expensesTable.getValueAt(i, 0),
													(ExpenseCategory) expensesTable.getValueAt(i, 1),
													cal,
													(BigDecimal)expensesTable.getValueAt(i, 3));							
			boolean successDelete = financialBalance.deleteExpense(expenseToDelete);
			if (successDelete) rowsToDelete[j++] = i;
		}
		SwingUtilities.invokeLater(new RowRemover(rowsToDelete));	// schedules the row-removing process
		
		// TODO: Update monthly reports' table.
	}

	//Inner listeners
	class AddButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().equals(addButton)) {
				addEnteredExpense();
			}
		}	
	}
	
	// private members
	// a helper inner class responsible for user-friendly text box interaction
	private class NameAndPriceFieldListener implements FocusListener
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
	
	// table event listener
	private class DeletePressedListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent arg0) {}

		@Override
		public void keyReleased(KeyEvent ke) {
			if (ke.getKeyCode()==KeyEvent.VK_DELETE && expensesTable.getSelectedRowCount() > 0)
			{
				int decision = JOptionPane.showConfirmDialog(mainPanel, "Are You sure, You want to delete selected expense(s) from database?", "Delete selected expense(s)", JOptionPane.YES_NO_OPTION);
				if (decision == JOptionPane.YES_NO_OPTION)
				{
					deleteSelectedExpenses();
				}
			}
		}

		@Override
		public void keyTyped(KeyEvent arg0) {}
	}
	
	// window closed listener
	private class WindowClosingListener implements WindowListener{

		@Override
		public void windowActivated(WindowEvent e) {}

		@Override
		public void windowClosed(WindowEvent e) {}

		@Override
		public void windowClosing(WindowEvent e) {
			financialBalance.close();			
		}

		@Override
		public void windowDeactivated(WindowEvent e) {}

		@Override
		public void windowDeiconified(WindowEvent e) {}

		@Override
		public void windowIconified(WindowEvent e) {}

		@Override
		public void windowOpened(WindowEvent e) {}
	}
	
	class StatisticsMenuListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().equals(statisticsMenuItem)){
				SwingUtilities.invokeLater(new PlotFrameRunner(financialBalance.getMonthlyReports()));
			}
			
		}
	}
	
	// a row removing utility working on a separate thread
	private class RowRemover implements Runnable{
		private int[] rowsToRemove;
		
		public RowRemover(int[] rowsToRemove){
			this.rowsToRemove = rowsToRemove;
		}
		
		@Override
		public void run(){
			synchronized (expensesTable){
				DefaultCellEditor defaultCellEditor = (DefaultCellEditor)expensesTable.getCellEditor(); 
				if (defaultCellEditor != null) defaultCellEditor.stopCellEditing();	// IMPORTANT! Otherwise the operation won't be completed successfully
				DefaultTableModel model = (DefaultTableModel) expensesTable.getModel();
				for (int i = rowsToRemove.length-1; i>=0; i--) model.removeRow(rowsToRemove[i]);	// Rows are removed in lowering index order, otherwise the inappropriate rows would be removed.
			}
		}
	}
			
	// inner application logic
	private FinancialBalance financialBalance;
	
	// GUI elements
	// panels
	private JPanel mainPanel;
	private JPanel addPanel;
	
	// scroll panes
	private JScrollPane tableScrollPane;
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
	
	// other private members
	private String defaultName = "Insert the expense name here...               ";
	private int baseWidth = 850;
	private int baseHeight = 500;
	
	// required by JFrame
	private static final long serialVersionUID = 6750128568375064611L;
}
