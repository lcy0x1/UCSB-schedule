package page;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import util.P;

public abstract class Page extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static int size(int x, int y, int a) {
		return Math.min(a * x / 2300, a * y / 1300);
	}

	private final Page front;

	private boolean resizing = false;

	protected Page(Page p) {
		front = p;
		setLayout(null);
	}

	public void callBack(Object newParam) {
	}

	public synchronized final void componentResized(int x, int y) {
		if (resizing)
			return;
		resizing = true;
		resized(x, y);
		if (front != null)
			front.componentResized(x, y);
		Component[] cs = getComponents();
		for (Component c : cs) {
			if (c instanceof Page)
				((Page) c).componentResized(x, y);
			fontSetter(c);
		}
		repaint();
		resizing = false;
	}

	public final Page getFront() {
		return front;
	}

	public final P getXY() {
		JRootPane jrp = MainFrame.F.getRootPane();
		return new P(jrp.getWidth(), jrp.getHeight());
	}

	public final void resized() {
		Point p = getXY().toPoint();
		componentResized(p.x, p.y);
	}

	protected synchronized void changePanel(Page p) {
		MainFrame.changePanel(p);
	}

	protected void exit() {
	}

	protected final void exitAll() {
		exit();
		if (front != null)
			front.exitAll();
	}

	protected final Page getRootPage() {
		return front == null ? this : front.getRootPage();
	}

	protected final Page getThis() {
		return this;
	}

	protected void keyPressed(KeyEvent e) {
	}

	protected void keyReleased(KeyEvent e) {
	}

	protected void keyTyped(KeyEvent e) {
	}

	protected void leave() {
	}

	protected void mouseClicked(MouseEvent e) {
	}

	protected void mouseDragged(MouseEvent e) {
	}

	protected void mouseMoved(MouseEvent e) {
	}

	protected void mousePressed(MouseEvent e) {
	}

	protected void mouseReleased(MouseEvent e) {
	}

	protected void mouseWheel(MouseEvent e) {
	}

	protected void renew() {
	}

	protected abstract void resized(int x, int y);

	protected void set(Component jc, int winx, int winy, int x, int y, int w, int h) {
		jc.setBounds(x * winx / 2300, y * winy / 1300, w * winx / 2300, h * winy / 1300);
	}

	protected synchronized void timer(int t) {
		resized();
	}

	protected void windowActivated() {
	}

	protected void windowDeactivated() {
	}

	private void fontSetter(Component c) {
		c.setFont(MainFrame.font);
		if (c instanceof JScrollPane) {
			JScrollPane jsp = (JScrollPane) c;
			fontSetter(jsp.getViewport().getView());
		} else if (c instanceof JTable) {
			JTable t = (JTable) c;
			fontSetter(t.getTableHeader());
		} else if (c instanceof Container) {
			Container con = (Container) c;
			for (Component ic : con.getComponents())
				fontSetter(ic);

		}
	}

}
