package page;

import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import main.Analyzer;
import util.FreeTime;
import util.TimeTable;

public class FreeTimeTable extends JTable implements TableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] strs = new String[] { "name", "weekday", "start", "length", "min duration" };

	private final TimeTable tt;
	private final Page page;

	protected FreeTimeTable(Page p, TimeTable tat) {
		tt = tat;
		page = p;
		setModel(this);
	}

	@Override
	public void addTableModelListener(TableModelListener arg0) {
	}

	@Override
	public Class<?> getColumnClass(int c) {
		if (c < 2)
			return String.class;
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
		return tt.empt.size();
	}

	@Override
	public Object getValueAt(int r, int c) {
		FreeTime et = tt.empt.get(r);
		if (c == 0)
			return et.name;
		else if (c == 1)
			return Analyzer.fromWek(et.wek);
		else if (c == 2)
			return Analyzer.encode(et.sta);
		else if (c == 3)
			return et.len;
		else if (c == 4)
			return et.dur;
		else
			return null;
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
		FreeTime et = tt.empt.get(r);
		if (c == 0) {
			String str = (String) o;
			if (str.trim().length() > 0)
				et.name = str.trim();
		} else if (c == 1) {
			int val = Analyzer.toWek((String) o);
			if (val > 0)
				et.wek = val;
		} else if (c == 2)
			et.sta = Analyzer.decode((int) o);
		else if (c == 3)
			et.len = (int) o;
		else if (c == 4)
			et.dur = (int) o;
		page.callBack(null);
	}

}
