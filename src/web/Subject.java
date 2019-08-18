package web;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;

import io.InStream;
import io.OutStream;

public class Subject {

	public final String name;

	public final List<Course> list = new ArrayList<Course>();

	protected Subject(InStream is) {
		name = is.nextString();
		int n = is.nextInt();
		for (int i = 0; i < n; i++)
			list.add(new Course(is));
	}

	protected Subject(String str, Element table) {
		name = str;
		Course course = null;
		for (Element e : table.children())
			if (e.attr("class").equals("courseSearchHeader")) {
				course = new Course(e);
				list.add(course);
			} else if (e.attr("class").equals("courseSearchItem"))
				course.list.add(new Lecture(e));
	}

	@Override
	public String toString() {
		return name + " - " + list.size();
	}

	protected OutStream write() {
		OutStream os = new OutStream();
		os.writeString(name);
		os.writeInt(list.size());
		for (Course c : list)
			c.write(os);
		os.terminate();
		return os;
	}

}
