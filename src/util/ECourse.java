package util;

import io.InStream;
import io.OutStream;
import io.Reader;
import main.Analyzer;
import main.Printer;
import web.Time;

public class ECourse {

	protected static ECourse read(Category c, InStream is) {
		int ver = is.nextInt();
		if (ver >= 1)
			return read$001(c, is);
		Printer.e("Course", 42, "error in reading");
		return null;
	}

	private static ECourse read$001(Category c, InStream is) {
		String str = is.nextString();
		int id = is.nextInt();
		int dur = is.nextInt();
		int[] sts = is.nextIntsB();
		String pos = is.nextString();
		ECourse cor = new ECourse(str, id, dur, sts, c);
		cor.pos = pos;
		return cor;
	}

	public final String name;
	public final int id, duration;

	public final int[] starts;

	public final Category c;

	public String pos = "";

	public ECourse(Category category, String str, String ID, Time time, int sec) {
		c = category;
		name = str;
		id = Reader.parseIntN(ID);
		duration = time.len();
		starts = new int[20];
		for (int i = 0; i < 20; i++)
			if ((time.date & (1 << i % 5)) > 0 && (sec & (1 << i / 5)) > 0)
				starts[i] = Analyzer.decode(time.strt);
			else
				starts[i] = 0;
	}

	private ECourse(String str, int ID, int dur, int[] sta, Category cat) {
		c = cat;
		name = str;
		starts = sta;
		id = ID;
		duration = dur;
	}

	@Override
	public String toString() {
		return name + " " + Analyzer.getTime(starts) + "-" + duration + " " + pos;
	}

	protected OutStream write() {
		OutStream os = new OutStream();
		os.writeInt(1);
		os.writeString(name);
		os.writeInt(id);
		os.writeInt(duration);
		os.writeInts(starts);
		os.writeString(pos);
		os.terminate();
		return os;
	}

}
