package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.InStream;
import io.OutStream;
import main.Analyzer;
import main.Printer;

public class FreeTime {

	public String name = "free time";

	/**
	 * configuration:<br>
	 * wek = weekdays, binary;<br>
	 * sta = starting time, in minutes from 0:00;<br>
	 * len = length, in minutes;<br>
	 * dur = minimum continuous free duration
	 */
	public int wek, sta, len, dur;

	/** currently selected classes, only used for calculation */
	private List<ECourse> lc = new ArrayList<ECourse>();

	/** discretization of time */
	private Set<Integer> times = new TreeSet<Integer>();

	/** constructor: create a new FreeTime object based on configurations */
	public FreeTime(String str, int week, int star, int leng, int dura) {
		name = str;
		wek = week;
		sta = Analyzer.decode(star);
		len = leng;
		dur = dura;
	}

	/** read a FreeTime object from Stream */
	protected FreeTime(InStream is) {
		zread(is);
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * when a new class is added to schedule, add it to the FreeTime overlap class
	 * list if overlaps
	 */
	protected void addCheck(ECourse c) {
		if (include(c))
			lc.add(c);
	}

	/** initialize run-time utilities before calculation */
	protected void clear() {
		lc.clear();
		times.clear();
	}

	/**
	 * see if a class conflicts with current course selection and free time policy
	 */
	protected boolean conflict(ECourse c) {
		// add start time and end time of this FreeTime policy to discretization
		if (times.size() == 0) {
			times.add(sta);
			times.add(sta + len);
		}

		// add class to class list
		lc.add(c);

		// loop though each weekday (and each session for summer)
		for (int i = 0; i < 20; i++) {
			int t = c.starts[i];
			int t1 = t + c.duration;

			// if time overlaps with the FreeTime policy
			if (t != 0 && (wek & (1 << i % 5)) > 0 && t < sta + len && t1 > sta) {

				// add the time point within the range to discretization
				if (t > sta)
					times.add(t);
				if (t1 < sta + len)
					times.add(t1);

				// check each class, calculate the free time using discretization
				Integer[] pot = times.toArray(new Integer[0]); // discrete time
				boolean[] itv = new boolean[pot.length - 1]; // time interval
				for (ECourse o : lc) {
					int t0 = o.starts[i];
					if (t0 != 0) {
						int p0 = 0;
						int t2 = t0 + o.duration;
						while (t0 > pot[p0])
							p0++;
						int p1 = pot.length - 1;
						while (t2 < pot[p1])
							p1--;
						for (int j = p0; j < p1; j++)
							itv[j] = true;
					}
				}

				// check if the is a continuous free time interval exceeding the FreeTime policy
				int fill = 0;
				boolean succ = false;
				for (int j = 0; j < itv.length; j++)
					if (!itv[j]) {
						fill += pot[j + 1] - pot[j];
						if (fill >= dur) {
							succ = true;
						}
					} else
						fill = 0;

				// remove the class if it failed this policy, don't need to remove
				// discretization
				if (!succ) {
					lc.remove(c);
					return true;
				}
			}
		}

		// will be added later, so remove it first
		lc.remove(c);
		return false;
	}

	/** see if a class overlaps with its time interval */
	protected boolean include(ECourse c) {
		for (int i = 0; i < 20; i++) {
			int t = c.starts[i];
			if (t != 0 && (wek & (1 << i % 5)) > 0 && t < sta + len && t + c.duration > sta)
				return true;
		}
		return false;
	}

	protected void remove(ECourse c) {
		lc.remove(c);
	}

	protected OutStream write() {
		OutStream os = new OutStream();
		os.writeInt(2);
		os.writeString(name);
		os.writeInt(wek);
		os.writeInt(sta);
		os.writeInt(len);
		os.writeInt(dur);
		os.terminate();
		return os;
	}

	private void zread(InStream is) {
		int val = is.nextInt();
		if (val >= 2)
			zread$002(is);
		else if (val >= 1)
			zread$001(is);
		else
			Printer.e("Empty", 33, "error in reading");
	}

	private void zread$001(InStream is) {
		wek = is.nextInt();
		sta = is.nextInt();
		len = is.nextInt();
		dur = is.nextInt();
	}

	private void zread$002(InStream is) {
		name = is.nextString();
		wek = is.nextInt();
		sta = is.nextInt();
		len = is.nextInt();
		dur = is.nextInt();
	}

}
