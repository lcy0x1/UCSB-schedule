package util;

import java.util.ArrayList;
import java.util.List;

import io.InStream;
import io.OutStream;
import main.Analyzer;

public class TimeTable {

	protected static final int EARLY = 0, LATE = 1, INTER = 2, EMPTY = 3, MAX = 180;

	public static final List<TimeTable> tables = new ArrayList<TimeTable>();

	public final List<Category> list = new ArrayList<Category>();
	public final List<FreeTime> empt = new ArrayList<FreeTime>();
	public final List<Solution> map = new ArrayList<Solution>();
	public final List<Solution> rmap = new ArrayList<Solution>();

	public String name = "schedule";
	private List<ECourse> chosens = new ArrayList<ECourse>();
	private Solution curs = null;
	private int early, late, inter;
	public int[][] vals;

	public TimeTable() {
	}

	public TimeTable(String str, InStream is) {
		zread(is);
		name = str;
	}

	public void apply(Solution s) {
		chosens.clear();
		for (ECourse c : s.courses)
			chosens.add(c);
	}

	/** input: scoring policy, time in h:m format */
	public void search(int[][] vs) {
		// initialization
		map.clear();
		rmap.clear();
		chosens.clear();
		for (FreeTime et : empt)
			et.clear();

		// convert the time format into minutes from 0:00
		vals = vs;
		early = Analyzer.decode(vs[0][0]);
		late = Analyzer.decode(vs[1][0]);
		inter = vs[2][0];

		if (list.size() == 0) {
			// no class added
			Solution s = new Solution(new ArrayList<ECourse>(), vs[2][2]);
			rmap.add(s);
			map.add(s);
		} else {
			// calculate recursively
			list.get(0).proceed(this, list, 0, vs[2][2]);

			// organize results
			chosens.clear();
			// remove the lecture solutions with no possible section solution
			map.removeIf(s -> s.map.size() == 0);
			map.forEach(s -> rmap.addAll(s.map));
		}

		// sort solutions
		map.sort(null);
		rmap.sort(null);
	}

	@Override
	public String toString() {
		return name;
	}

	public OutStream write() {
		OutStream os = new OutStream();
		os.writeInt(1);
		os.writeInt(vals == null ? 0 : 1);
		if (vals != null)
			os.writeIntss(vals);
		os.writeInt(list.size());
		for (Category c : list)
			os.accept(c.write());
		os.writeInt(empt.size());
		for (FreeTime et : empt)
			os.accept(et.write());
		os.terminate();
		return os;
	}

	/** a class is proved to have no conflicts with schedule or policies */
	protected void addCourse(ECourse c) {
		chosens.add(c);
		for (FreeTime et : empt)
			et.addCheck(c);

	}

	/** called when proceed reaches its end, a solution without sections added */
	protected void addSolution(int dura) {
		map.add(curs = new Solution(chosens, getValue(dura)));
		// continue claculation of sections
		list.get(0).rproceed(this, list, 0, dura);
	}

	/** return the minimum break interval if class c is added */
	protected int conflict(ECourse c) {
		int temp = 2000;

		// see if it violates the early / late policy
		for (int i = 0; i < 20; i++) {
			int t = c.starts[i];
			if (t != 0) {
				if (t < early)
					return -1;
				if (t + c.duration > late)
					return -1;
			}
		}

		// see if it violates the FreeTime policies
		for (FreeTime et : empt)
			if (et.conflict(c))
				return -1;

		// see if they overlap, and find the minimum break
		for (ECourse c0 : chosens)
			for (int i = 0; i < 20; i++)
				if (c0.starts[i] != 0) {
					int dst = c0.starts[i] - c.starts[i];
					if (dst < 0) {
						int t0 = -dst - c0.duration - inter;
						if (t0 < 0)
							return -1;
						temp = Math.min(temp, t0);
					} else {
						int t0 = dst - c.duration - inter;
						if (t0 < 0)
							return -1;
						temp = Math.min(temp, t0);
					}
				}
		return temp;
	}

	/** called when rproceed reaches its end, a solution with sections added */
	protected void finalSection(int dura) {
		curs.map.add(new Solution(chosens, getValue(dura)));
	}

	/**
	 * get the value of current solution, minimum duration is added as a parameter
	 */
	protected int getValue(int dura) {
		int[] earlys = new int[20];
		int[] lates = new int[20];
		for (int i = 0; i < 20; i++) {
			earlys[i] = vals[0][2];
			lates[i] = vals[1][2];
		}
		for (ECourse c : chosens)
			for (int i = 0; i < 20; i++)
				if (c.starts[i] != 0) {
					earlys[i] = Math.min(earlys[i], c.starts[i] - early);
					lates[i] = Math.min(lates[i], late - c.starts[i] - c.duration);
				}
		int[] ans = new int[3];
		for (int i = 0; i < 20; i++) {
			ans[0] += earlys[i];
			ans[1] += lates[i];
		}
		ans[2] = dura;
		return vals[0][1] * ans[0] + vals[1][1] * ans[1] + vals[2][1] * ans[2];
	}

	protected void removeCourse(ECourse c) {
		chosens.remove(c);
		for (FreeTime et : empt)
			et.remove(c);
	}

	private void zread(InStream is) {
		int ver = is.nextInt();
		if (ver >= 1)
			zread$001(is);
	}

	private void zread$001(InStream is) {
		if (is.nextInt() == 1)
			vals = is.nextIntsBB();
		int n = is.nextInt();
		for (int i = 0; i < n; i++)
			list.add(new Category(this, is.subStream()));
		n = is.nextInt();
		for (int i = 0; i < n; i++)
			empt.add(new FreeTime(is.subStream()));
	}

}
