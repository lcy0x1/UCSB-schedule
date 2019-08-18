package page;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import web.Course;
import web.Lecture;
import web.Quarter;
import web.Section;
import web.Subject;

public class CourseInfoPage extends Page {

	private static final long serialVersionUID = 1L;

	private final JButton back = new JButton("back");
	private final JButton read = new JButton("read");

	private final JList<String> jlq = new JList<String>(Quarter.quas);
	private final JList<String> jlb = new JList<String>(Quarter.strs);
	private final JList<Course> jlc = new JList<Course>();
	private final JList<Lecture> jll = new JList<Lecture>();
	private final JList<Section> jls = new JList<Section>();

	private final JScrollPane jspq = new JScrollPane(jlq);
	private final JScrollPane jspb = new JScrollPane(jlb);
	private final JScrollPane jspc = new JScrollPane(jlc);
	private final JScrollPane jspl = new JScrollPane(jll);
	private final JScrollPane jsps = new JScrollPane(jls);

	private boolean changing;

	protected CourseInfoPage(Page p) {
		super(p);
		setBackground(Color.WHITE);
		ini();
		resized();
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
		set(jspq, x, y, 50, 100, 200, 200);
		set(jspb, x, y, 50, 350, 200, 900);
		set(read, x, y, 300, 0, 200, 50);
		set(jspc, x, y, 300, 100, 1000, 1150);
		set(jspl, x, y, 1350, 100, 900, 500);
		set(jsps, x, y, 1350, 650, 900, 600);
	}

	protected Course selected() {
		return jlc.getSelectedValue();
	}

	private void addListeners() {
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changePanel(getFront());
			}
		});

		read.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changing = true;
				setSelection(true);
				changing = false;
			}
		});

		jlb.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (changing || jlb.getValueIsAdjusting())
					return;
				changing = true;
				setSelection(false);
				changing = false;
			}

		});

		jlq.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (changing || jlq.getValueIsAdjusting())
					return;
				changing = true;
				setSelection(false);
				changing = false;
			}

		});

		jlc.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (changing || jlc.getValueIsAdjusting())
					return;
				changing = true;
				setCourse(jlc.getSelectedValue());
				changing = false;
			}

		});

		jll.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (changing || jll.getValueIsAdjusting())
					return;
				changing = true;
				setLecture(jll.getSelectedValue());
				changing = false;
			}

		});

	}

	private void ini() {
		add(back);
		add(read);
		add(jspq);
		add(jspb);
		add(jspc);
		add(jspl);
		add(jsps);
		read.setEnabled(false);
		addListeners();
	}

	private void setCourse(Course c) {
		jll.clearSelection();
		if (c == null) {
			jll.setListData(new Lecture[0]);
			setLecture(null);
			return;
		}
		jll.setListData(c.list.toArray(new Lecture[0]));
		jll.setSelectedIndex(0);
		setLecture(jll.getSelectedValue());
	}

	private void setLecture(Lecture l) {
		jls.clearSelection();
		if (l == null || l.secs.length == 0) {
			jls.setListData(new Section[0]);
			return;
		}
		jls.setListData(l.secs);
	}

	private void setSelection(boolean create) {
		String q = jlq.getSelectedValue();
		String b = jlb.getSelectedValue();
		if (q == null || b == null) {
			setSubject(null);
			read.setEnabled(false);
			return;
		}
		Subject s = Quarter.getQuarter(q).getSubject(b, create);
		read.setEnabled(s == null);
		setSubject(s);
	}

	private void setSubject(Subject b) {
		jlc.clearSelection();
		if (b == null) {
			jlc.setListData(new Course[0]);
			setCourse(null);
			return;
		}
		jlc.setListData(b.list.toArray(new Course[0]));
		jlc.setSelectedIndex(0);
		setCourse(jlc.getSelectedValue());
	}

}
