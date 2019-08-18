package main;

public class Analyzer {

	private static final char[] chs = new char[] { 'M', 'T', 'W', 'R', 'F' };

	public static int decode(int t) {
		return t / 100 * 60 + t % 100;
	}

	public static int encode(int t) {
		return t / 60 * 100 + t % 60;
	}

	public static String fromWek(int wek) {
		String ans = "";
		for (int i = 0; i < 5; i++)
			if ((wek & (1 << i)) > 0)
				ans += chs[i];
		return ans;
	}

	public static int[] getSec(int[] sta) {
		int[] ans = new int[2];
		ans[0] = -1;
		for (int i = 0; i < 20; i++)
			if (sta[i] > 0) {
				if (ans[0] < 0)
					ans[0] = i / 5;
				ans[1] = i / 5 - ans[0] + 1;
			}
		return ans;
	}

	public static String getTime(int[] sta) {
		String wek = "";
		int st = 0;
		for (int i = 0; i < 20; i++)
			if (sta[i] > 0) {
				st = sta[i];
				if (wek.indexOf(chs[i % 5]) < 0)
					wek += chs[i % 5];
			}
		String m = "" + st % 60;
		if (m.length() == 1)
			m = "0" + m;
		return wek + " " + st / 60 + ":" + m;

	}

	public static int toWek(String str) {
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

}
