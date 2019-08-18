package util;

import java.util.ArrayList;
import java.util.List;

import io.InStream;
import io.OutStream;
import main.Printer;
import web.Course;
import web.Lecture;

public class Category {

	protected final TimeTable tt;
	public final List<ECourse> list = new ArrayList<ECourse>();

	public String name;
	public ECourse[][] abide;
	public float color = (float) Math.random();
	private int chosen = 0;

	public Category(TimeTable table, Course c) {
		tt = table;
		name = c.name.split(" - ")[0];
		if (c.list.get(0).secs.length > 0)
			abide = new ECourse[c.list.size()][];
		for (int i = 0; i < c.list.size(); i++) {
			Lecture l = c.list.get(i);
			ECourse ec = new ECourse(this, name, l.main.id, l.main.time, c.section);
			list.add(ec);
			if (abide != null) {
				abide[i] = new ECourse[l.secs.length];
				for (int j = 0; j < l.secs.length; j++)
					abide[i][j] = new ECourse(this, name, l.secs[j].id, l.secs[j].time, c.section);
			}
		}
	}

	protected Category(TimeTable tb, InStream is) {
		tt = tb;
		zread(is);
	}

	public void add(ECourse c) {
		list.add(c);
	}

	@Override
	public String toString() {
		return name;
	}

	/** calculate solutions recursively, try each lecture */
	protected void proceed(TimeTable b, List<Category> lp, int index, int temp) {
		if (list.size() > 0)
			for (int i = 0; i < list.size(); i++) {
				ECourse choc = list.get(chosen = i);

				// check conflicts
				int inter = b.conflict(choc);
				if (inter < 0)
					continue;
				if (inter > temp)
					inter = temp;

				// add the class to TimeTable, proceed or end
				b.addCourse(choc);
				if (index + 1 < lp.size())
					lp.get(index + 1).proceed(b, lp, index + 1, inter);
				else
					b.addSolution(inter);
				b.removeCourse(choc);
			}
		else if (index + 1 < lp.size())
			lp.get(index + 1).proceed(b, lp, index + 1, temp);
		else
			b.addSolution(temp);
	}

	/** calculate solutions recursively, try each section */
	protected void rproceed(TimeTable b, List<Category> lp, int index, int temp) {
		// same algorithm as proceed
		int i = chosen;
		if (abide != null)
			for (int j = 0; j < abide[i].length; j++) {
				ECourse rchoc = abide[i][j];
				int rint = b.conflict(rchoc);
				if (rint < 0)
					continue;
				if (rint > temp)
					rint = temp;
				b.addCourse(rchoc);
				if (index + 1 < lp.size())
					lp.get(index + 1).rproceed(b, lp, index + 1, rint);
				else
					b.finalSection(rint);
				b.removeCourse(rchoc);
			}
		else if (index + 1 < lp.size())
			lp.get(index + 1).rproceed(b, lp, index + 1, temp);
		else
			b.finalSection(temp);
	}

	protected OutStream write() {
		OutStream os = new OutStream();
		os.writeInt(1);
		os.writeString(name);
		os.writeInt(abide == null ? 0 : 1);
		os.writeInt(list.size());
		for (int i = 0; i < list.size(); i++) {
			os.accept(list.get(i).write());
			if (abide != null) {
				os.writeInt(abide[i].length);
				for (int j = 0; j < abide[i].length; j++)
					os.accept(abide[i][j].write());
			}
		}
		os.writeFloat(color);
		os.terminate();
		return os;
	}

	private void zread(InStream is) {
		int ver = is.nextInt();
		if (ver >= 1)
			zread$001(is);
		else
			Printer.e("Category", 135, "error in reading");
	}

	private void zread$001(InStream is) {

		name = is.nextString();
		boolean abi = is.nextInt() == 1;
		int n = is.nextInt();
		if (abi)
			abide = new ECourse[n][];
		for (int i = 0; i < n; i++) {
			add(ECourse.read(this, is.subStream()));
			if (abi) {
				int m = is.nextInt();
				abide[i] = new ECourse[m];
				for (int j = 0; j < m; j++)
					abide[i][j] = ECourse.read(this, is.subStream());
			}
		}
		color = is.nextFloat();

	}

}
