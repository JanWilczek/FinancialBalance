package FinancialBalance;

import javax.swing.table.AbstractTableModel;

public class ExpenseTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -5713013989357777200L;
	private String [] columnNames;
	private Object[][] data;
	
	//Public constructor
	public ExpenseTableModel(Object[][] data, String[] columnNames)
	{
		super();
		if (data == null)
		{
			throw new NullPointerException("Data passed to the table is null.");
		} else if (columnNames == null)
		{
			throw new NullPointerException("Column names passed to this table is null.");
		} else{
			this.columnNames = columnNames;
			this.data = data;	
		}
	}
	
	//Inherited member functions
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public Object getValueAt(int row, int column) {
		return data[row][column];
	}

}
