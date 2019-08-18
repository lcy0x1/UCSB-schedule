package page;

import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class SearchTable extends JTable implements TableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] strs = new String[] { "early", "late", "break" };

	protected int[][] vals = new int[][] { { 800, 1000, 180 }, { 2200, 100, 180 }, { 10, 2000, 20 } };

	protected SearchTable() {
		setModel(this);
	}

	protected SearchTable(int[][] vs) {
		if (vs != null)
			vals = vs;
		setModel(this);
	}

	@Override
	public void addTableModelListener(TableModelListener arg0) {
	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		return Integer.class;
	}

	@Override
	public int getColumnCount() {
		return strs.length;
	}

	@Override
	public String getColumnName(int c) {
		return strs[c];
	}

	@Override
	public int getRowCount() {
		return 3;
	}

	@Override
	public Object getValueAt(int r, int c) {
		return vals[c][r];
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return true;
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {
	}

	@Override
	public void setValueAt(Object o, int r, int c) {
		int val = (int) o;
		if (val < 0)
			return;
		vals[c][r] = val;
	}

}
