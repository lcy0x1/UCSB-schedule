package web;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.InStream;
import io.OutStream;

public class Lecture {

	public final Section main;
	public final Section[] secs;

	protected Lecture(Element e) {
		main = new Section(e);
		Elements ss = e.getElementsByAttributeValue("class", "row susbSessionItem");
		secs = new Section[ss.size()];
		for (int i = 0; i < ss.size(); i++)
			secs[i] = new Section(ss.get(i));
	}

	protected Lecture(InStream is) {
		main = new Section(is.subStream());
		int n = is.nextInt();
		secs = new Section[n];
		for (int i = 0; i < n; i++)
			secs[i] = new Section(is.subStream());
	}

	protected Lecture(Lecture l) {
		main = l.main;
		List<Section> list = new ArrayList<Section>();
		for (Section s : l.secs)
			if (s.perfect())
				list.add(s);
		secs = list.toArray(new Section[0]);
	}

	@Override
	public String toString() {
		return main.toString();
	}

	protected void write(OutStream os) {
		os.accept(main.write());
		os.writeInt(secs.length);
		for (Section s : secs)
			os.accept(s.write());
	}

}
