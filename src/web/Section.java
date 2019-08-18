package web;

import org.jsoup.nodes.Element;

import io.InStream;
import io.OutStream;

public class Section {

	private static String extract(Element e, String str) {
		return e.getElementsByAttributeValueContaining("class", str).get(0).ownText();
	}

	public final String id, space, max;
	public final String loca, ld0, ld1;
	public final String[] prof;

	public final Time time;

	public boolean cancel = false;

	protected Section(Element e) {
		id = extract(e, "col-sm-1");
		String days = extract(e, "days");
		if (cancel = days.equals("Cancel")) {
			prof = new String[] { loca = ld0 = ld1 = space = max = "Cancel" };
			time = null;
			return;
		}
		time = new Time(days, extract(e, "time"));
		String temp = extract(e, "location");
		if (temp.length() == 0) {
			Element a = e.getElementsByClass("mapLink").get(0);
			loca = a.ownText();
			ld0 = a.attr("data-buildingcode");
			ld1 = a.attr("data-room");
		} else
			loca = ld0 = ld1 = "";
		prof = e.getElementsByAttributeValueContaining("class", "instructor").get(0).getElementsByTag("span").get(0)
				.html().split("<br>");
		space = extract(e, "search-space");
		max = extract(e, "lg-days");
	}

	protected Section(InStream is) {
		id = is.nextString();
		cancel = is.nextInt() == 1;
		loca = is.nextString();
		ld0 = is.nextString();
		ld1 = is.nextString();
		space = is.nextString();
		max = is.nextString();
		int n = is.nextInt();
		prof = new String[n];
		for (int i = 0; i < n; i++)
			prof[i] = is.nextString();
		int t = is.nextInt();
		time = t == -1 ? null : new Time(t);
	}

	@Override
	public String toString() {
		if (cancel)
			return id + ", Cancel";
		String profs = "";
		for (String pro : prof)
			profs += ", " + pro;
		return id + ", " + time + ", " + loca + profs + ", " + space + ", " + max;
	}

	protected boolean perfect() {
		return !cancel && !time.tba;
	}

	protected OutStream write() {
		OutStream os = new OutStream();
		os.writeString(id);
		os.writeInt(cancel ? 1 : 0);
		os.writeString(loca);
		os.writeString(ld0);
		os.writeString(ld1);
		os.writeString(space);
		os.writeString(max);
		os.writeInt(prof.length);
		for (String str : prof)
			os.writeString(str);
		os.writeInt(time == null ? -1 : time.write());
		os.terminate();
		return os;
	}

}
