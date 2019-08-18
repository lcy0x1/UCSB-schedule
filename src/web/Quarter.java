package web;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import io.InStream;
import io.OutStream;
import io.Reader;
import io.Writer;

public class Quarter {

	public static final String[] strs = new String[] { "ANTH ", "ART  ", "ARTHI", "ARTST", "AS AM", "ASTRO", "BIOL ",
			"BMSE ", "BL ST", "CH E ", "CHEM ", "CH ST", "CHIN ", "CLASS", "COMM ", "C LIT", "CMPSC", "CMPTG", "CNCSP",
			"DANCE", "DYNS ", "EARTH", "EACS ", "EEMB ", "ECON ", "ED   ", "ECE  ", "ENGR ", "ENGL ", "ESM  ", "ENV S",
			"ESS  ", "ES   ", "FEMST", "FAMST", "FR   ", "GEN S", "GEOG ", "GER  ", "GPS  ", "GLOBL", "GRAD ", "GREEK",
			"HEB  ", "HIST ", "INT  ", "ITAL ", "JAPAN", "KOR  ", "LATIN", "LAIS ", "LING ", "LIT  ", "MARSC", "MATRL",
			"MATH ", "ME   ", "MAT  ", "ME ST", "MES  ", "MS   ", "MCDB ", "MUS  ", "MUS A", "PHIL ", "PHYS ", "POL S",
			"PORT ", "PSY  ", "RG ST", "RENST", "SLAV ", "SOC  ", "SPAN ", "SHS  ", "PSTAT", "TMP  ", "THTR ", "WRIT ",
			"W&L  " };

	public static final String[] quas = new String[] { "20194", "20193", "20192", "20191" };

	private static final Map<String, Quarter> quarters = new TreeMap<String, Quarter>();

	public static Quarter getQuarter(String id) {
		if (quarters.containsKey(id))
			return quarters.get(id);
		Quarter qua = new Quarter(id);
		quarters.put(id, qua);
		return qua;
	}

	public static void readAll() {
		File f = new File("./user/class.data");
		if (!f.exists())
			return;
		InStream is = Reader.readBytes(f);
		int n = is.nextInt();
		for (int i = 0; i < n; i++) {
			Quarter q = new Quarter(is);
			quarters.put(q.id, q);
		}
	}

	public static void writeAll() {
		OutStream os = new OutStream();
		os.writeInt(quarters.size());
		for (Quarter q : quarters.values())
			q.write(os);
		os.terminate();
		Writer.writeBytes(os, "./user/class.data");
	}

	private final String id;

	private final Map<String, Subject> map = new TreeMap<String, Subject>();

	private Quarter(InStream is) {
		id = is.nextString();
		int n = is.nextInt();
		for (int i = 0; i < n; i++) {
			Subject s = new Subject(is.subStream());
			map.put(s.name, s);
		}
	}

	private Quarter(String str) {
		id = str;
	}

	public Subject getSubject(String ind, boolean create) {
		if (map.containsKey(ind))
			return map.get(ind);
		if (!create)
			return null;
		Client client = Client.getClient();
		Document doc = Jsoup.parse(client.getData(id, ind));
		Elements eles = doc.getElementsByAttributeValue("class", "datatableNew");
		if (eles.size() == 0)
			return null;
		Subject ans = new Subject(ind, eles.get(0));
		map.put(ind, ans);
		return ans;
	}

	private void write(OutStream os) {
		os.writeString(id);
		os.writeInt(map.size());
		for (Subject s : map.values())
			os.accept(s.write());
	}

}
