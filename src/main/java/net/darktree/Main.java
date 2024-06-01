package net.darktree;

import javax.swing.*;

public class Main extends JFrame {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			System.err.println("Failed to initialize theme. Using fallback.");
		}

		MainWindow mainWindow = new MainWindow();
		try {
			mainWindow.openFile("src/main/java/net/darktree/Main.java");
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		SwingUtilities.invokeLater(() -> {
			mainWindow.setVisible(true);
		});
	}

}