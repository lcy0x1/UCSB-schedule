package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import util.ECourse;
import util.FreeTime;
import util.Solution;
import util.TimeTable;

public class Painter {

	private static final int p0x = 40, p0y = 20, p1x = 10, p1y = 6, p2x = 10, p2y = 20;

	private static final int alp = 64, w1 = 10, t0 = 8;

	private static final int dx = 50, dy = 30, cx = 125, cy = 50, x = 5, y = 14;
	private static final int tx = dx + cx * x, ty = dy + cy * y;
	private static final String[] strs = new String[] { "Mon", "Tue", "Wed", "Thr", "Fri" };

	public static BufferedImage draw(TimeTable tt, Solution s) {
		BufferedImage bimg = new BufferedImage(tx, ty, BufferedImage.TYPE_INT_RGB);
		Graphics g = bimg.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, tx, ty);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, tx - 1, ty - 1);
		for (int i = 0; i < x; i++)
			g.drawString(strs[i], dx + cx * i + p0x, p0y);
		for (int i = 0; i < y; i++)
			g.drawString(t0 + i + ":00", p1x, dy + cy * i + p1y);

		g.setColor(Color.GRAY);
		for (int i = 0; i < x; i++)
			g.drawLine(dx + cx * i, 0, dx + cx * i, ty);
		for (int i = 0; i < y; i++)
			g.drawLine(dx, dy + cy * i, tx, dy + cy * i);

		for (FreeTime et : tt.empt) {
			for (int i = 0; i < 5; i++)
				if ((et.wek & (1 << i)) > 0) {
					int x0 = dx + cx * i + 1;
					int y0 = dy + cy * (et.sta - t0 * 60) / 60 + 1;
					int w0 = cx - 1;
					int h0 = cy * et.len / 60 - 1;
					g.setColor(new Color(128, 128, 128, alp));
					g.fillRect(x0, y0, w0, h0);
				}
		}
		if (s == null)
			return bimg;
		if (s.map.size() == 0)
			uniSol(g, s.courses);
		else
			multiSol(g, s);
		return bimg;
	}

	private static void multiSol(Graphics g, Solution s) {
		uniSol(g, s.courses);
		Set<ECourse> set = new HashSet<ECourse>();
		for (ECourse c : s.courses)
			set.add(c);
		int ind = 0;
		int tot = s.map.size();
		Map<ECourse, Integer> map = new HashMap<ECourse, Integer>();
		for (Solution sol : s.map) {
			for (ECourse c : sol.courses)
				if (!set.contains(c))
					if (map.containsKey(c))
						map.put(c, map.get(c) + 1);
					else
						map.put(c, 1);
		}

		for (ECourse c : map.keySet())
			if (map.get(c) == tot) {
				uniSol(g, c);
				set.add(c);
			}

		for (Solution sol : s.map) {
			for (ECourse c : sol.courses)
				if (!set.contains(c)) {
					Color col = Color.getHSBColor(c.c.color, 1, 1);
					Color tra = new Color(col.getRed(), col.getGreen(), col.getBlue(), alp);
					boolean printed = false;
					for (int i = 0; i < 20; i++) {
						if (c.starts[i] > 0) {
							int x0 = dx + cx * (i % 5) + 1 + cx * ind / tot;
							int y0 = dy + cy * (c.starts[i] - t0 * 60) / 60 + 1;
							int w0 = cx / tot;
							int h0 = cy * c.duration / 60 - 1;
							g.setColor(tra);
							g.fillRect(x0, y0, w0, h0);
							printed = true;
						}
						if (i % 5 == 4 && printed)
							break;
					}
				}
			ind++;
		}
	}

	private static void uniSol(Graphics g, ECourse... courses) {
		for (ECourse c : courses) {
			Color col = Color.getHSBColor(c.c.color, 1, 1);
			Color tra = new Color(col.getRed(), col.getGreen(), col.getBlue(), alp);
			boolean printed = false;
			for (int i = 0; i < 20; i++) {
				if (c.starts[i] > 0) {
					int[] sec = Analyzer.getSec(c.starts);
					int x0 = dx + cx * (i % 5) + cx * sec[0] / 4 + 1;
					int y0 = dy + cy * (c.starts[i] - t0 * 60) / 60 + 1;
					int w0 = cx * sec[1] / 4 - 1;
					int h0 = cy * c.duration / 60 - 1;
					g.setColor(tra);
					g.fillRect(x0, y0, w0, h0);
					g.setColor(col);
					g.fillRect(x0, y0, w1, h0);
					g.setColor(Color.BLACK);
					y0 += p2y * 2 * sec[0];
					g.drawString(c.name + " " + c.id, x0 + p2x, y0 + p2y);
					g.drawString(c.pos, x0 + p2x, y0 + p2y * 2);
					printed = true;
				}
				if (i % 5 == 4 && printed)
					break;
			}
		}
	}

}
