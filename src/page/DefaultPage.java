package page;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class DefaultPage extends Page {

	private static final long serialVersionUID = 1L;

	private final JButton back = new JButton("back");

	protected DefaultPage(Page p) {
		super(p);
		setBackground(Color.WHITE);
		ini();
		resized();
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
	}

	private void addListeners() {
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changePanel(getFront());
			}
		});

	}

	private void ini() {
		add(back);
		addListeners();
	}

}
