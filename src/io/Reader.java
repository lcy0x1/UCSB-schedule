package io;

import static java.lang.Character.isDigit;

import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import main.Printer;
import page.MainFrame;
import util.TimeTable;
import web.Quarter;

public class Reader extends DataIO {

	public static void getData() {
		try {
			readInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Quarter.readAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			readData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int parseIntN(String str) {
		int ans;
		try {
			ans = parseInt(str);
		} catch (Exception e) {
			ans = -1;
		}
		return ans;
	}

	public static int[] parseIntsN(String str) {
		ArrayList<String> lstr = new ArrayList<String>();
		int t = -1;
		for (int i = 0; i < str.length(); i++)
			if (t == -1) {
				if (isDigit(str.charAt(i)) || str.charAt(i) == '-' || str.charAt(i) == '+')
					t = i;
			} else if (!isDigit(str.charAt(i))) {
				lstr.add(str.substring(t, i));
				t = -1;
			}
		if (t != -1)
			lstr.add(str.substring(t));
		int ind = 0;
		while (ind < lstr.size()) {
			if (isDigit(lstr.get(ind).charAt(0)) || lstr.get(ind).length() > 1)
				ind++;
			else
				lstr.remove(ind);
		}
		int[] ans = new int[lstr.size()];
		for (int i = 0; i < lstr.size(); i++)
			ans[i] = Integer.parseInt(lstr.get(i));
		return ans;
	}

	public static InStream readBytes(File file) {
		InStream is = null;
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			in = new BufferedInputStream(in);
			byte[] len = new byte[4];
			in.read(len);
			int length = toInt(translate(len), 0);
			byte[] bs = new byte[length];
			in.read(bs);
			is = new InStream(translate(bs));
			in.close();
		} catch (IOException e1) {
			Printer.r(123, "IOE: " + file.getPath());
			if (in != null)
				try {
					in.close();
				} catch (IOException e2) {
					Printer.r(127, "cannot close input");
					e2.printStackTrace();
				}
			e1.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e1) {
					Printer.r(135, "cannot close input");
					e1.printStackTrace();
				}
		}
		return is;
	}

	public static Queue<String> readLines(File file) {
		Queue<String> ans = new ArrayDeque<String>();
		BufferedReader reader = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			reader = new BufferedReader(isr);
			String temp = null;
			while ((temp = reader.readLine()) != null)
				ans.add(temp);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ans;
	}

	private static int parseInt(String str) {
		return parseInts(1, str)[0];
	}

	private static int[] parseInts(int n, String str) {
		ArrayList<String> lstr = new ArrayList<String>();
		int t = -1;
		for (int i = 0; i < str.length(); i++)
			if (t == -1) {
				if (isDigit(str.charAt(i)) || str.charAt(i) == '-' || str.charAt(i) == '+')
					t = i;
			} else if (!isDigit(str.charAt(i))) {
				lstr.add(str.substring(t, i));
				t = -1;
			}
		if (t != -1)
			lstr.add(str.substring(t));
		int ind = 0;
		while (ind < lstr.size()) {
			if (isDigit(lstr.get(ind).charAt(0)) || lstr.get(ind).length() > 1)
				ind++;
			else
				lstr.remove(ind);
		}
		int[] ans = new int[n];
		for (int i = lstr.size() - n; i < lstr.size(); i++)
			ans[i - lstr.size() + n] = Integer.parseInt(lstr.get(i));
		return ans;
	}

	private static void readData() {
		File f = new File("user/");
		if (f.exists()) {
			File[] fs = f.listFiles();
			for (File fi : fs) {
				String str = fi.getName();
				if (str.endsWith(".sche")) {
					str = str.substring(0, str.length() - 5);
					TimeTable.tables.add(new TimeTable(str, readBytes(fi)));
				}
			}
		}
		if (TimeTable.tables.size() == 0)
			TimeTable.tables.add(new TimeTable());
	}

	private static void readInfo() {
		try {
			File f = new File("./user/data.ini");
			if (f.exists()) {
				Queue<String> qs = readLines(f);
				int[] r = parseInts(4, qs.poll());
				MainFrame.crect = new Rectangle(r[0], r[1], r[2], r[3]);
			} else {
				f.getParentFile().mkdirs();
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
