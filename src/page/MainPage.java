package page;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import util.TimeTable;

public class MainPage extends Page {

	private static final long serialVersionUID = 1L;

	private final JButton sche = new JButton("see schedule");
	private final JButton find = new JButton("find course");
	private final JList<TimeTable> jlt = new JList<TimeTable>(TimeTable.tables.toArray(new TimeTable[0]));
	private final JScrollPane jspt = new JScrollPane(jlt);
	private final JButton add = new JButton("add");
	private final JButton rem = new JButton("remove");
	private final JTextField jtf = new JTextField();// TODO

	protected MainPage() {
		super(null);
		setBackground(Color.WHITE);
		ini();
		resized();
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(sche, x, y, 400, 100, 200, 50);
		set(find, x, y, 650, 100, 200, 50);
		set(add, x, y, 400, 200, 200, 50);
		set(rem, x, y, 400, 300, 200, 50);
		set(jspt, x, y, 50, 100, 300, 500);
	}

	private void addListeners() {

		sche.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TimeTable tt = jlt.getSelectedValue();
				if (tt == null)
					return;
				changePanel(new SchedulePage(getThis(), tt));
			}
		});

		find.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changePanel(new CourseInfoPage(getThis()));
			}
		});

		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TimeTable tt = new TimeTable();
				TimeTable.tables.add(tt);
				jlt.setListData(TimeTable.tables.toArray(new TimeTable[0]));
				jlt.setSelectedValue(tt, true);
			}
		});

		rem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TimeTable tt = jlt.getSelectedValue();
				if (tt == null)
					return;
				TimeTable.tables.remove(tt);
				File f = new File("user/" + tt.name + ".sche");
				f.delete();
				jlt.setListData(TimeTable.tables.toArray(new TimeTable[0]));
				jlt.setSelectedIndex(0);
			}
		});

	}

	private void ini() {
		add(sche);
		add(find);
		add(jspt);
		add(add);
		add(rem);
		add(jtf);
		addListeners();
	}

}
