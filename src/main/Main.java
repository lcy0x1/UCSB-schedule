package main;

import io.Reader;
import io.Writer;
import page.MainFrame;

public class Main {

	public static boolean write = false;

	public static void main(String[] args) {
		Writer.logSetup();
		Reader.getData();
		new MainFrame("schedule").initialize();
	}

}
