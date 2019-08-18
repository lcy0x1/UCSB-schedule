package web;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.InStream;
import io.OutStream;

public class Course {

	public static final int[] SECS = new int[] { 3, 12, 15, 1, 2, 4, 8 };

	public final String name, unit, grade, sec;
	public final List<Lecture> list = new ArrayList<Lecture>();

	public final int section;

	protected Course(Element e) {
		name = e.getElementsByClass("courseTitle").get(0).ownText();
		Elements eles = e.getElementsByAttributeValueStarting("id", "pageContent_CourseList_sessionLabel_");
		if (eles.size() > 0)
			sec = eles.get(0).text().substring(8, 9);
		else
			sec = "C";
		section = SECS[sec.charAt(0) - 'A'];
		Elements es = e.getElementsByClass("pr5");
		unit = es.get(0).ownText();
		grade = es.get(1).ownText();

	}

	protected Course(InStream is) {
		name = is.nextString();
		unit = is.nextString();
		grade = is.nextString();
		sec = is.nextString();
		section = SECS[sec.charAt(0) - 'A'];
		int n = is.nextInt();
		for (int i = 0; i < n; i++)
			list.add(new Lecture(is));
	}

	private Course(Course c) {
		name = c.name;
		unit = c.unit;
		grade = c.grade;
		sec = c.sec;
		section = c.section;
	}

	public Course getPerfect() {
		Course ans = new Course(this);
		for (Lecture l : list)
			if (l.main.perfect())
				ans.list.add(new Lecture(l));
		return ans;
	}

	@Override
	public String toString() {
		return name + ", " + unit + ", " + grade + ", " + sec;
	}

	protected void write(OutStream os) {
		os.writeString(name);
		os.writeString(unit);
		os.writeString(grade);
		os.writeString(sec);
		os.writeInt(list.size());
		for (Lecture l : list)
			l.write(os);
	}

}
