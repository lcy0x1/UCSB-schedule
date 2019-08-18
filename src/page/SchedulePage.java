package page;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import main.Painter;
import util.Category;
import util.FreeTime;
import util.Solution;
import util.TimeTable;

public class SchedulePage extends Page {

	private static final long serialVersionUID = 1L;

	private final JButton back = new JButton("back");

	private final JList<Category> jlc = new JList<Category>();
	private final JScrollPane jspc = new JScrollPane(jlc);
	private final JList<FreeTime> jle = new JList<FreeTime>();
	private final JScrollPane jspe = new JScrollPane(jle);
	private final JList<Solution> jls = new JList<Solution>();
	private final JScrollPane jsps = new JScrollPane(jls);

	private final JLabel table = new JLabel();
	private final JScrollPane jsp = new JScrollPane(table);
	private final JToggleButton hsec = new JToggleButton("hide all section");
	private final JToggleButton group = new JToggleButton("group sections");
	private final JButton color = new JButton("reset color");
	private final JButton adcat = new JButton("class detail");
	private final JButton adfre = new JButton("free time detail");
	private final JButton fresh = new JButton("calculate");
	private final JToggleButton zoom = new JToggleButton("see sections");

	private final SearchTable jt;
	private final JScrollPane jspt;

	private final TimeTable tt;

	private boolean changing = false;

	public SchedulePage(Page p, TimeTable tat) {
		super(p);
		setBackground(Color.WHITE);
		tt = tat;
		jt = new SearchTable(tt.vals);
		jspt = new JScrollPane(jt);
		ini();
		resized();
	}

	@Override
	protected void renew() {
		jlc.setListData(tt.list.toArray(new Category[0]));
		jle.setListData(tt.empt.toArray(new FreeTime[0]));
		if (tt.vals != null)
			tt.search(tt.vals);
		jls.setListData(tt.rmap.toArray(new Solution[0]));
		jls.setSelectedIndex(0);
		hsec.setSelected(false);
		group.setSelected(false);
		zoom.setSelected(false);
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
		set(jsps, x, y, 50, 50, 350, 1200);
		set(jsp, x, y, 450, 50, 1200, 1200);
		set(jspt, x, y, 1700, 50, 450, 200);

		set(fresh, x, y, 1950, 300, 200, 50);
		set(hsec, x, y, 1950, 400, 200, 50);
		set(group, x, y, 1950, 500, 200, 50);
		set(zoom, x, y, 1950, 600, 200, 50);
		set(color, x, y, 1950, 800, 200, 50);

		set(adcat, x, y, 1700, 300, 200, 50);
		set(jspc, x, y, 1700, 400, 200, 250);
		set(adfre, x, y, 1700, 700, 200, 50);
		set(jspe, x, y, 1700, 800, 200, 250);

		jt.setRowHeight(size(x, y, 50));
	}

	private void addListeners() {

		back.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				changePanel(getFront());
			}

		});

		color.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int n = tt.list.size();
				float f = (float) Math.random();
				for (int i = 0; i < n; i++) {
					tt.list.get(i).color = f;
					f += 1.0f / n;
					if (f >= 1)
						f--;
				}
				setSele();
			}

		});

		adcat.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				changePanel(new CategoryEditPage(getThis(), tt));
			}

		});

		adfre.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				changePanel(new EmptyTimeEditPage(getThis(), tt));
			}

		});

		hsec.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (group.isSelected()) {
					hsec.setSelected(false);
					return;
				}
				if (!hsec.isSelected())
					zoom.setSelected(false);
				changing = true;
				List<Solution> ls = hsec.isSelected() ? tt.map : tt.rmap;
				jls.setListData(ls.toArray(new Solution[0]));
				jls.setSelectedIndex(0);
				setSele();
				changing = false;
			}

		});

		group.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (jlc.getSelectedValue() == null) {
					group.setSelected(false);
					return;
				}
				groups();
			}

		});

		fresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				changing = true;
				group.setSelected(false);
				hsec.setSelected(false);
				tt.search(jt.vals);
				jls.setListData(tt.rmap.toArray(new Solution[0]));
				if (tt.rmap.size() > 0)
					jls.setSelectedIndex(0);
				setSele();
				changing = false;
			}

		});

		zoom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!zoom.isSelected()) {
					if (group.isSelected()) {
						groups();
						return;
					}
					List<Solution> ls = hsec.isSelected() ? tt.map : tt.rmap;
					jls.setListData(ls.toArray(new Solution[0]));
					jls.setSelectedIndex(0);
					setSele();
					return;
				}
				Solution s = jls.getSelectedValue();
				if (s == null || s.map.size() == 0) {
					zoom.setSelected(false);
					return;
				}
				changing = true;
				jls.setListData(s.map.toArray(new Solution[0]));
				jls.setSelectedIndex(0);
				setSele();
				changing = false;
			}

		});

		jls.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (changing || arg0.getValueIsAdjusting())
					return;
				setSele();
			}

		});

		jlc.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (changing || arg0.getValueIsAdjusting())
					return;
				if (group.isSelected())
					groups();
			}

		});
	}

	private void groups() {
		changing = true;
		zoom.setSelected(false);
		hsec.setSelected(false);
		List<Solution> ls = tt.rmap;
		if (group.isSelected()) {
			Collection<Solution> cs = Solution.orgSec(ls, jlc.getSelectedValue());
			ls = new ArrayList<Solution>();
			ls.addAll(cs);
			ls.sort(null);
		}
		jls.setListData(ls.toArray(new Solution[0]));
		jls.setSelectedIndex(0);
		setSele();
		changing = false;
	}

	private void ini() {
		add(jsp);
		add(jsps);
		add(hsec);
		add(group);
		add(color);
		add(jspc);
		add(adcat);
		add(jspt);
		add(fresh);
		add(zoom);
		add(jspe);
		add(adfre);
		add(back);
		table.setBorder(BorderFactory.createEtchedBorder());
		table.setHorizontalAlignment(SwingConstants.LEFT);
		table.setVerticalAlignment(SwingConstants.TOP);

		setSele();
		addListeners();
	}

	private void setSele() {
		table.setIcon(new ImageIcon(Painter.draw(tt, jls.getSelectedValue())));
	}

}
