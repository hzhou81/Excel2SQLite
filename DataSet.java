package com.sucem.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * <p>
 * ����: ����ģ��
 * </p>
 * 
 * <p>
 * ����: ��ResultSetת����JTable��ʶ���ģ��
 * </p>
 * 
 * <p>
 * �汾: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * ��˾: �Ϻ����ֳ������г����޹�˾
 * </p>
 * 
 * @����: �����
 * @version 1.0
 */
public class DataSet implements TableModel {
	int columnCount;

	Vector columnNames = new Vector();

	Vector rowData = new Vector();

	Vector tableData = new Vector();
	
	protected EventListenerList listenerList = new EventListenerList();

	public DataSet(ResultSet resultSet) throws DataNotFoundException,
			SQLException {
		boolean wasNull = true;
		if (resultSet != null) {
			try {
				ResultSetMetaData rsmd = resultSet.getMetaData();
				columnCount = rsmd.getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					columnNames.addElement(rsmd.getColumnName(i));
				}
				while (resultSet.next()) {
					wasNull = false;
					rowData = new Vector(columnCount);
					for (int j = 1; j <= columnCount; j++) {
						rowData.addElement(resultSet.getObject(j));
					}
					tableData.addElement(rowData);
				}
				if (wasNull) {
					// throw new SucemException("û������");
					throw new DataNotFoundException();
				}
			}finally{
				resultSet.close();
				resultSet.getStatement().close();
			}
		} else {
			throw new DataNotFoundException();
		}
	}

	/**
	 * �����鹹��һ��DataSet
	 * 
	 * @param rowData
	 *            Object[][]
	 * @param columnNames
	 *            Object[]
	 * @todo ���ת�����ݵ�
	 */
	public DataSet(Object rowData[][], Object columnNames[]) {
		
		
	}

	public DataSet(IDataSet ds) throws DataNotFoundException{
		// boolean wasNull = true;
		if(ds!=null){
			// ��ȡ��ͷ
			IDataRow dr=ds.getRow(0);
			String[] columns=dr.getColumns();
			for(int i=0;i<columns.length;i++){
				columnNames.add(columns[i]);
			}
			tableData=new Vector();
			for(int j=0;j<ds.getRowCount();j++){
				rowData=new Vector();
				dr=ds.getRow(j);
				for(int k=0;k<dr.getColumnCount();k++){
					rowData.add(dr.getValue((String) columnNames.get(k)));
				}
				tableData.add(rowData);
			}
		}else{
			throw new DataNotFoundException();
		}
		
	}
	
	public DataSet() {
	}

	public int getColumnCount() {
		if(columnNames==null){
			return 0;
		}else return columnNames.size();
	}

	public int getRowCount() {
		if(tableData==null){
			return 0;
		}else return tableData.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Vector theRow = (Vector) tableData.elementAt(rowIndex);
		return theRow.elementAt(columnIndex);
	}

	public String getColumnName(int column) {
		// return columnNames.elementAt(column).toString();
		String columnName = columnNames.elementAt(column).toString();
		return columnName;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Vector theRow = (Vector) tableData.elementAt(rowIndex);
		theRow.setElementAt(aValue, columnIndex);
	}

	public Class getColumnClass(int columnIndex) {
		return columnNames.elementAt(columnIndex).getClass();
	}

	public void addTableModelListener(TableModelListener l) {
	}

	public void removeTableModelListener(TableModelListener l) {
	}

	/**
	 * ������һ��Excel�ļ�
	 * 
	 * @param FileName
	 *            String Excel�ļ���
	 * @param rowIndex
	 *            int ������Excel��ָ�����п�ʼ
	 * @param columnIndex
	 *            int ������Excel��ָ�����п�ʼ
	 */
	public void Export2Excel(String FileName, int rowIndex, int columnIndex) {

	}

	/**
	 * ������һ���ı��ļ�
	 * 
	 * @param FileName
	 *            String �ı��ļ���
	 * @param IncludeTitle
	 *            boolean
	 */
	public void Export2Txt(String FileName, boolean IncludeTitle) {

	}

	/**
	 * ������Xml��������Xml�ļ�������������
	 */
	public void Export2Xml() {

	}
	/**
	 * <p>�����������ģ���е����������<code>newIdentifiers</code>�������ȵ�ǰ������Ļ�
     * �򽫶��ڵ�����ӵ�����ģ���С����<code>newIdentifiers</code>�ȵ�ǰ�����ٵĻ���
     * �к������н����޳���<p>
     *
     * @param   newIdentifiers  �������� 
     *				���Ϊ <code>null</code>, �����и�����0
	 */
	public void setColumnIdentifiers(Object[] newIdentifiers) {
		setColumnIdentifiers(convertToVector(newIdentifiers));
	}
	/**
	 * <p>�����������ģ���е����������<code>newIdentifiers</code>�������ȵ�ǰ������Ļ�
     * �򽫶��ڵ�����ӵ�����ģ���С����<code>newIdentifiers</code>�ȵ�ǰ�����ٵĻ���
     * �к������н����޳���<p>
     *  @param   columnIdentifiers  vector of column identifiers.  If
     *				<code>null</code>, set the model
     *                          to zero columns
	 * @param columnIdentifiers
	 */
	public void setColumnIdentifiers(Vector columnIdentifiers) {
		setDataVector(tableData, columnIdentifiers); 
	}
	/**
	 * ���غ�����һ�����ݵ�Vector
     * @param anArray  Ҫת��������
     * @return  �µ�Vector; ��� <code>anArray</code> Ϊ <code>null</code>,
     *				���� <code>null</code>
	 * @param anArray
	 * @return
	 */
	protected static Vector convertToVector(Object[] anArray) {
        if (anArray == null) { 
            return null;
        }
        Vector v = new Vector(anArray.length);
        for (int i=0; i < anArray.length; i++) {
            v.addElement(anArray[i]);
        }
        return v;
    }
	/**
	 * �����е�<code>dataVector</code>�滻Ϊ�µ����ݣ�<code>dataVector</code>.
     *  <code>columnIdentifiers</code>����Щ�����������֡�
     *
     * @param   dataVector         �µı�����
     * @param   columnIdentifiers     �µı�ͷ
     * @see #getDataVector
     */
	public void setDataVector(Vector dataVector, Vector columnIdentifiers) {
        this.tableData = nonNullVector(dataVector);
        this.columnNames = nonNullVector(columnIdentifiers); 
        justifyRows(0, getRowCount()); 
        // fireTableStructureChanged();
    }
	
	private void justifyRows(int from, int to) { 
		tableData.setSize(getRowCount()); 
	    for (int i = from; i < to; i++) { 
		    if (tableData.elementAt(i) == null) { 
		    	tableData.setElementAt(new Vector(), i); 
		    }
		    ((Vector)tableData.elementAt(i)).setSize(getColumnCount());
		}
	}
	
	private static Vector nonNullVector(Vector v) { 
		return (v != null) ? v : new Vector(); 
	} 
	/*
	public void fireTableStructureChanged() {
        fireTableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
    }

    public void fireTableChanged(TableModelEvent e) {
    	// Guaranteed to return a non-null array
    	Object[] listeners = listenerList.getListenerList();
    	// Process the listeners last to first, notifying
    	// those that are interested in this event
    	for (int i = listeners.length-2; i>=0; i-=2) {
    	    if (listeners[i]==TableModelListener.class) {
    		((TableModelListener)listeners[i+1]).tableChanged(e);
    	    }
    	}
    }
    */
	
	public Vector getDataVector() {
        return tableData;
    }
	
	public void setRowCount(int rowCount) { 
		setNumRows(rowCount); 
	} 
	
	
    public void setNumRows(int rowCount) { 
        int old = getRowCount();
	if (old == rowCount) { 
	    return; 
	}
	tableData.setSize(rowCount);
        if (rowCount <= old) {
          
        }
        else {
	    justifyRows(old, rowCount); 
        }
    }
    
    public void addRow(Vector rowData) {
        insertRow(getRowCount(), rowData);
    }
    
    public void insertRow(int row, Vector rowData) {
    	tableData.insertElementAt(rowData, row); 
    	justifyRows(row, row+1); 
            // fireTableRowsInserted(row, row);
     }
}
