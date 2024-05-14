package net.darktree;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Scanner;

public class Main extends JFrame {

	private JButton button1;
	private JButton button2;
	private JButton button3;

	private String getExampleText() {
		String content = "Hello World!\n\n";
		content += "SELECT * FROM Examples WHERE Ratify = TRUE;\n\n";

		content += "Begin\n";
		content += " . \n".repeat(32);
		content += "End\n";

		return content;
	}

	private void registerLocalFont(String path) {
		try {
			GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
			Objects.requireNonNull(stream);
			environment.registerFont(Font.createFont(Font.TRUETYPE_FONT, stream));
		} catch(FontFormatException | IOException e){
			e.printStackTrace();
		}
	}

	public Main() {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		registerLocalFont("fonts/JetBrainsMono-Regular.ttf");
		Font font = new Font("JetBrains Mono Regular", Font.PLAIN, 13);

		System.out.println("Available Fonts:");
		for (Font name : env.getAllFonts()) {
			System.out.println(" * " + name.getName());
		}

		// default text style
		StyleContext styleContext = new StyleContext();
		Style defaultStyle = styleContext.getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setLineSpacing(defaultStyle, 1.2f);

		// style of highlighted words
		Style highlightStyle = styleContext.addStyle("ConstantWidth", null);
		StyleConstants.setForeground(highlightStyle, new Color(0x729fcf));
		StyleConstants.setLineSpacing(highlightStyle, 1.2f);

		// we set the font to the whole panel
		// no need to do it here
		// StyleConstants.setFontFamily(defaultStyle, font.getFamily());
		// StyleConstants.setFontFamily(highlightStyle, font.getFamily());

		setTitle("Ratify | Untitled Document");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		// needed to make the scroll panel work as intended
		JPanel wrapperPanel = new JPanel();
		wrapperPanel.setLayout(new GridLayout(1, 1));
		wrapperPanel.setPreferredSize(new Dimension(500, 500));

		KeywordStyledDocument styled = new KeywordStyledDocument(defaultStyle, highlightStyle);

		try {
			styled.insertString(0, getExampleText(), null);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		// the actual textbox
		final JTextPane pane = new JTextPane(styled);
		pane.setFont(font);

		// scroll bars panel
		JScrollPane scrollPane = new ModernScrollPane(pane);
		scrollPane.getVerticalScrollBar().setUnitIncrement(13 * 2);

		// doesn't really work, idk why
		// scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		// link it all together
		wrapperPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.add(wrapperPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		// example buttons
		button1 = new JButton("Button 1");
		button2 = new JButton("Button 2");
		button3 = new JButton("Button 3");

		buttonPanel.add(button1);
		buttonPanel.add(button2);
		buttonPanel.add(button3);

		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		add(mainPanel);
		pack();
		setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		URPClient client = new URPClient("localhost");

		while (!client.isConnected()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Ready");

		Scanner scanner = new Scanner(System.in);
		while (client.isConnected()) {
			String line = scanner.nextLine();
			if (line.equals("exit")) {
				break;
			} else if (line.startsWith("join")) {
				int gid = Integer.parseInt(line.split(" ")[1]);
				int pass = Integer.parseInt(line.split(" ")[2]);
				client.join(gid, pass);
			}
		}

		client.close();

		/*try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			System.err.println("Failed to initialize theme. Using fallback.");
		}

		SwingUtilities.invokeLater(() -> {
			Main app = new Main();
			app.setVisible(true);
		});
		 */
	}

}