package web;

public class Time {

	private static final char[] chs = new char[] { 'M', 'T', 'W', 'R', 'F' };

	private static String fromWek(int wek) {
		String ans = "";
		for (int i = 0; i < 5; i++)
			if ((wek & (1 << i)) > 0)
				ans += chs[i];
		return ans;
	}

	private static String timeStr(int time) {
		String ans = "";
		int hr = time / 100;
		ans += hr < 10 ? "0" + hr : hr;
		int mn = time % 100;
		ans += ":";
		ans += mn < 10 ? "0" + mn : mn;
		return ans;
	}

	private static int toWek(String str) {
		char[] cwek = str.toCharArray();
		int iwek = 0;
		int j = 0;
		for (int i = 0; i < cwek.length; i++) {
			while (j < chs.length && chs[j] != cwek[i])
				j++;
			if (j == chs.length)
				return -1;
			iwek |= 1 << j;
		}
		return iwek;
	}

	public int date, strt, endt;

	protected boolean tba;

	protected Time(int is) {
		endt = is % 2400;
		is /= 2400;
		strt = is % 2400;
		date = is / 2400;
	}

	protected Time(String days, String time) {
		if (tba = days.equals("T.B.A."))
			return;
		date = toWek(days);
		String[] strs = time.split(" ");
		String p0 = strs[0];
		String[] mid = strs[1].split("-");
		String a0 = mid[0];
		String p1 = mid[1];
		String a1 = strs[2];
		int t0 = 0, t1 = 0;
		String[] t0p = p0.split(":");
		t0 = Integer.parseInt(t0p[0]) * 100 + Integer.parseInt(t0p[1]);
		if (a0.equals("PM") && t0 < 1200)
			t0 += 1200;
		String[] t1p = p1.split(":");
		t1 = Integer.parseInt(t1p[0]) * 100 + Integer.parseInt(t1p[1]);
		if (a1.equals("PM") && t1 < 1200)
			t1 += 1200;
		strt = t0;
		endt = t1;
	}

	public int len() {
		int s = strt / 100 * 60 + strt % 100;
		int e = endt / 100 * 60 + endt % 100;
		return e - s;
	}

	@Override
	public String toString() {
		if (tba)
			return "T.B.A.";
		return fromWek(date) + " " + timeStr(strt) + "-" + timeStr(endt);
	}

	protected int write() {
		return (date * 2400 + strt) * 2400 + endt;
	}

}
