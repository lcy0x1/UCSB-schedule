package page;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import main.Painter;
import util.FreeTime;
import util.TimeTable;

public class EmptyTimeEditPage extends Page {

	private static final long serialVersionUID = 1L;

	private final JButton back = new JButton("back");
	private final JButton adde = new JButton("new line");
	private final JButton reme = new JButton("remove");
	private final JLabel table = new JLabel();
	private final FreeTimeTable ftt;
	private final JScrollPane jspt;
	private final TimeTable tt;

	protected EmptyTimeEditPage(Page p, TimeTable tat) {
		super(p);
		setBackground(Color.WHITE);
		tt = tat;
		ftt = new FreeTimeTable(this, tt);
		jspt = new JScrollPane(ftt);
		ini();
		resized();
	}

	@Override
	public void callBack(Object o) {
		table.setIcon(new ImageIcon(Painter.draw(tt, null)));
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
		set(adde, x, y, 50, 100, 200, 50);
		set(reme, x, y, 300, 100, 200, 50);
		set(jspt, x, y, 50, 200, 800, 400);
		set(table, x, y, 1000, 0, 1200, 1200);
		ftt.setRowHeight(size(x, y, 50));
	}

	private void addListeners() {

		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changePanel(getFront());
			}
		});

		adde.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tt.empt.add(new FreeTime("new", 1, 1, 1, 1));
				callBack(null);
				resized();
			}
		});

		reme.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int r = ftt.getSelectedRow();
				if (r < 0)
					return;
				tt.empt.remove(r);
				callBack(null);
				resized();
			}
		});

	}

	private void ini() {
		add(back);
		add(adde);
		add(reme);
		add(jspt);
		add(table);
		callBack(null);
		addListeners();
	}

}
