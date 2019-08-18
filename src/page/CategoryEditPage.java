package page;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import util.Category;
import util.ECourse;
import util.TimeTable;
import web.Course;

public class CategoryEditPage extends Page {

	private static final long serialVersionUID = 1L;

	private final JButton back = new JButton("back");
	private final JButton addc = new JButton("add category");
	private final JButton remc = new JButton("remove");
	private final JButton remm = new JButton("remove class");
	private final JButton rems = new JButton("remove section");

	private final JList<Category> jlc = new JList<Category>();
	private final JScrollPane jspc = new JScrollPane(jlc);
	private final JList<ECourse> jlm = new JList<ECourse>();
	private final JScrollPane jspm = new JScrollPane(jlm);
	private final JList<ECourse> jls = new JList<ECourse>();
	private final JScrollPane jsps = new JScrollPane(jls);

	private final TimeTable tt;

	private CourseInfoPage cip = null;
	private Category cat = null;
	private boolean changing = false;

	protected CategoryEditPage(Page p, TimeTable tat) {
		super(p);
		setBackground(Color.WHITE);
		tt = tat;
		ini();
		resized();
	}

	@Override
	protected void renew() {
		if (cip != null) {
			Course c = cip.selected();
			cip = null;
			if (c == null)
				return;
			c = c.getPerfect();
			if (c.list.isEmpty())
				return;
			cat = new Category(tt, c);
			changing = true;
			tt.list.add(cat);
			jlc.setListData(tt.list.toArray(new Category[0]));
			jlc.setSelectedIndex(0);
			cat = jlc.getSelectedValue();
			setCat();
			changing = false;
		}
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
		set(jspm, x, y, 1400, 50, 600, 350);
		set(jsps, x, y, 1400, 450, 600, 800);
		set(remm, x, y, 2050, 650, 200, 50);
		set(rems, x, y, 2050, 750, 200, 50);
		set(addc, x, y, 2050, 50, 200, 50);
		set(remc, x, y, 2050, 150, 200, 50);
		set(jspc, x, y, 2050, 250, 200, 400);
	}

	private void addListeners() {

		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changePanel(getFront());
			}
		});

		addc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changePanel(cip = new CourseInfoPage(getThis()));
			}
		});

		remc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changing = true;
				tt.list.remove(cat);
				jlc.setListData(tt.list.toArray(new Category[0]));
				if (tt.list.size() > 0)
					jlc.setSelectedIndex(0);
				cat = jlc.getSelectedValue();
				setCat();
				changing = false;
			}
		});

		rems.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int m = jlm.getSelectedIndex();
				int s = jls.getSelectedIndex();
				if (m < 0 || s < 0)
					return;
				changing = true;
				ECourse[] cs = cat.abide[m];
				List<ECourse> lc = new ArrayList<ECourse>();
				for (int i = 0; i < cs.length; i++)
					if (i != s)
						lc.add(cs[i]);
				cat.abide[m] = lc.toArray(new ECourse[0]);
				setCat();
				changing = false;
			}
		});

		remm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int m = jlm.getSelectedIndex();
				if (m < 0)
					return;
				changing = true;
				if (cat.abide != null) {
					ECourse[][] cs = cat.abide;
					List<ECourse[]> lc = new ArrayList<ECourse[]>();
					for (int i = 0; i < cs.length; i++)
						if (i != m)
							lc.add(cs[i]);
					cat.abide = lc.toArray(new ECourse[0][]);
				}
				cat.list.remove(m);
				if (m >= cat.list.size())
					m = cat.list.size() - 1;
				setCat();
				jlm.setSelectedIndex(m);
				changing = false;
			}
		});

		jlc.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (changing || arg0.getValueIsAdjusting())
					return;
				changing = true;
				cat = jlc.getSelectedValue();
				setCat();
				changing = false;
			}

		});

		jlm.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (changing || arg0.getValueIsAdjusting())
					return;
				changing = true;
				if (jlm.getSelectedValue() == null)
					jlm.setListData(new ECourse[0]);
				else
					setSec(jlm.getSelectedIndex());
				changing = false;
			}

		});

	}

	private void ini() {
		add(back);
		add(jspm);
		add(jsps);
		add(addc);
		add(rems);
		add(remc);
		add(remm);
		add(jspc);
		jlc.setListData(tt.list.toArray(new Category[0]));
		addListeners();
	}

	private void setCat() {
		if (cat == null) {
			jlm.setListData(new ECourse[0]);
			jls.setListData(new ECourse[0]);
			return;
		}
		jlm.setListData(cat.list.toArray(new ECourse[0]));
		jlm.setSelectedIndex(0);
		setSec(0);
	}

	private void setSec(int ind) {
		if (cat.abide == null) {
			jls.setListData(new ECourse[0]);
			return;
		}
		jls.setListData(cat.abide[ind]);
		jls.setSelectedIndex(0);
	}

}
