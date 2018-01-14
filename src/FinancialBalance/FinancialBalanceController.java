package FinancialBalance;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import FinancialBalance.threading.PlotFrameRunner;

/**
 * 
 * @author Jan F. Wilczek
 * @date 14.01.18
 * @version 1.0
 * Class serving as a bridge between GUI (<code>FinancialBalanceView</code> class) and model (<code>Financial Balance</code> class).
 * 
 */
public class FinancialBalanceController {
	
	public FinancialBalanceController(FinancialBalance financialBalance, FinancialBalanceView financialBalanceView)
	{
		this.financialBalance = financialBalance;
		this.financialBalanceView = financialBalanceView;
		
		generateExpensesTable();
		generateReportsTable();
		
		// add necessary listeners
		this.financialBalanceView.getAddButton().addActionListener(ae -> addEnteredExpense());
		this.financialBalanceView.getExpensesTable().addKeyListener(new DeletePressedListener());
		this.financialBalanceView.getNameField().addKeyListener(new EnterPressedListener());
		this.financialBalanceView.getCategoryCombo().addKeyListener(new EnterPressedListener());
		Arrays.asList(this.financialBalanceView.getDateField().getEditor().getComponents()).forEach(component -> component.setFocusable(true));	// JSpinner and its components has 'focusable' set to false by default.
		((JSpinner.DefaultEditor)this.financialBalanceView.getDateField().getEditor()).getTextField().addKeyListener(new EnterPressedListener());
		this.financialBalanceView.getPriceField().addKeyListener(new EnterPressedListener());
		this.financialBalanceView.getStatisticsMenuItem().addActionListener(ae -> SwingUtilities.invokeLater(new PlotFrameRunner(this.financialBalance.getMonthlyReports())));
		this.financialBalanceView.addWindowListener(new WindowClosingListener());
		
		this.financialBalanceView.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.financialBalanceView.setVisible(true);
		
		this.financialBalanceView.getNameField().requestFocus();
	}
	
	private void addEnteredExpense() {
		Expense expenseToAdd = null;
		try {
			expenseToAdd = Expense.parseExpense(financialBalanceView.getNameField().getText(), financialBalanceView.getCategoryCombo().getSelectedItem().toString(), financialBalanceView.getSimpleDateFormat().format((Date)financialBalanceView.getDateField().getValue()), financialBalanceView.getPriceField().getValue().toString(), "yyyy/MM/dd");
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(financialBalanceView, nfe.getMessage() + " : Incorrect price format!", "Incorrect price format",JOptionPane.ERROR_MESSAGE);
		} catch (ParseException pe) {
			JOptionPane.showMessageDialog(financialBalanceView, pe.getMessage() + " : Incorrect date format!", "Incorrect date format", JOptionPane.ERROR_MESSAGE);
		}
		
		int index = financialBalance.addExpense(expenseToAdd);	// Add the expense to the main logic object.
		DefaultTableModel model = (DefaultTableModel) financialBalanceView.getExpensesTable().getModel();
		model.insertRow(index, new Object[] {expenseToAdd.getName(), expenseToAdd.getCategory(), financialBalanceView.getSimpleDateFormat().format(expenseToAdd.getDate().getTime()), expenseToAdd.getPrice()});

		// Reset priceField to its default. Leave the category and the date in case user wanted to add several objects with the same category or date. Set the focus to the name field.
		financialBalanceView.getNameField().setText("");
		financialBalanceView.getNameField().requestFocus();
		financialBalanceView.getPriceField().setValue("0.00");
	}
	
	private void deleteSelectedExpenses() {
		int[] selectedRows = financialBalanceView.getExpensesTable().getSelectedRows();
		int[] rowsToDelete = new int[selectedRows.length];
		int j = 0;
		for (int i : selectedRows) {
			Calendar cal = Calendar.getInstance();
			try{cal.setTime(financialBalanceView.getSimpleDateFormat().parse((String)financialBalanceView.getExpensesTable().getValueAt(i, 2)));}
			catch(ParseException pe){System.err.println(pe.getMessage()); return;	}
			Expense expenseToDelete = new Expense((String) financialBalanceView.getExpensesTable().getValueAt(i, 0),
													(ExpenseCategory) financialBalanceView.getExpensesTable().getValueAt(i, 1),
													cal,
													(BigDecimal)financialBalanceView.getExpensesTable().getValueAt(i, 3));							
			boolean successDelete = financialBalance.deleteExpense(expenseToDelete);
			if (successDelete) rowsToDelete[j++] = i;
		}
		SwingUtilities.invokeLater(new RowRemover(rowsToDelete));	// schedules the row-removing process
		
		// TODO: Update monthly reports' table.
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

		financialBalanceView.getReportsTable().setModel(new DefaultTableModel(reportsData, reportHeader));
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
					expensesData[i][2] = financialBalanceView.getSimpleDateFormat().format(expense.getDate().getTime());
					expensesData[i][3] = expense.getPrice();
					i++;
				}
				
			}
		
		//Create the table
		financialBalanceView.getExpensesTable().setModel(new DefaultTableModel(expensesData,columnNames));
		
		// Setting up columns' width
		TableColumn namesColumn = financialBalanceView.getExpensesTable().getColumnModel().getColumn(0);
		namesColumn.setPreferredWidth(400);	// Set the name field wide and with JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS make the next columns resized.
	}
	
	
	// a row removing utility working on a separate thread
	private class RowRemover implements Runnable{
		private int[] rowsToRemove;
		
		public RowRemover(int[] rowsToRemove){
			this.rowsToRemove = rowsToRemove;
		}
		
		@Override
		public void run(){
			synchronized (financialBalanceView.getExpensesTable()){
				DefaultCellEditor defaultCellEditor = (DefaultCellEditor) financialBalanceView.getExpensesTable().getCellEditor(); 
				if (defaultCellEditor != null) defaultCellEditor.stopCellEditing();	// IMPORTANT! Otherwise the operation won't be completed successfully
				DefaultTableModel model = (DefaultTableModel) financialBalanceView.getExpensesTable().getModel();
				for (int i = rowsToRemove.length-1; i>=0; i--) model.removeRow(rowsToRemove[i]);	// Rows are removed in lowering index order, otherwise the inappropriate rows would be removed.
			}
		}
	}
	
	// table event listener
	private class DeletePressedListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent arg0) {}

		@Override
		public void keyReleased(KeyEvent ke) {
			if (ke.getKeyCode()==KeyEvent.VK_DELETE && financialBalanceView.getExpensesTable().getSelectedRowCount() > 0)
			{
				int decision = JOptionPane.showConfirmDialog(financialBalanceView.getMainPanel(), "Are You sure, You want to delete selected expense(s) from database?", "Delete selected expense(s)", JOptionPane.YES_NO_OPTION);
				if (decision == JOptionPane.YES_NO_OPTION)
				{
					deleteSelectedExpenses();
				}
			}			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {}
	}
	
	// enter pressed event listener
	private class EnterPressedListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent arg0) {}

		@Override
		public void keyReleased(KeyEvent ke) {
			if (ke.getKeyCode()==KeyEvent.VK_ENTER)
			{
				addEnteredExpense();
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
	
	private FinancialBalance financialBalance;
	private FinancialBalanceView financialBalanceView;
}
