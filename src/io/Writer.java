package io;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import main.Main;
import main.Printer;
import page.MainFrame;
import util.TimeTable;
import web.Quarter;

public class Writer extends DataIO {

	private static File log;
	private static PrintStream ps;

	public static void check(File f) {
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		try {
			if (!f.exists())
				f.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void delete(File f) {
		if (f == null || !f.exists())
			return;
		if (f.isDirectory())
			for (File i : f.listFiles())
				delete(i);
		f.delete();
	}

	public static void logClose() {
		writeOptions();
		writeData();
		Quarter.writeAll();
		ps.close();
		if (log.length() == 0)
			log.deleteOnExit();
	}

	public static void logSetup() {
		String str = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		log = new File("./logs/" + str + ".log");
		if (!log.getParentFile().exists())
			log.getParentFile().mkdirs();
		try {
			if (!log.exists())
				log.createNewFile();
			ps = new PrintStream(log);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (Main.write) {
			System.setErr(ps);
			System.setOut(ps);
		}
	}

	public static void writeBytes(OutStream os, String path) {
		os.terminate();
		File f = new File(path);
		check(f);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			byte[] bs = os.getBytes();
			byte[] len = new byte[4];
			fromInt(len, 0, bs.length);
			fos.write(len);
			fos.write(bs);
			fos.close();
		} catch (IOException e) {
			Printer.w(130, "IOE!!!");
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e1) {
					Printer.w(131, "cannot close fos");
					e1.printStackTrace();
				}
			e.printStackTrace();
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e1) {
					Printer.w(139, "finally cannot close fos neither!");
					e1.printStackTrace();
				}
		}
	}

	private static PrintStream newFile(String str) {
		File f = new File(str);
		check(f);
		PrintStream out = null;
		try {
			out = new PrintStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return out;
	}

	private static void writeData() {
		for (TimeTable tt : TimeTable.tables)
			writeBytes(tt.write(), "user/" + tt.name + ".sche");
	}

	private static void writeOptions() {
		PrintStream out = newFile("./user/data.ini");
		Rectangle r = MainFrame.crect;
		out.println("rect= {" + r.x + ',' + r.y + ',' + r.width + ',' + r.height + '}');
		out.close();
	}

}
