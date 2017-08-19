package FinancialBalance;

import javax.swing.table.AbstractTableModel;

public class ExpenseTableModel extends AbstractTableModel {
	private String [] columnNames;
	private Object[][] data;
	
	//Public constructor
	public ExpenseTableModel(Object[][] data, String[] columnNames)
	{
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
	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
