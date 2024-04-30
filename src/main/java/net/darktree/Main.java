package net.darktree;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class Main extends JFrame {

	private static final int BOARD_SIZE = 10;
	private static final int TILE_SIZE = 50;

	private JButton button1;
	private JButton button2;
	private JButton button3;

	private BufferedImage createTileImage() {
		BufferedImage image = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();

		if ((new Random().nextBoolean())) {
			g2d.setColor(Color.RED);
		} else {
			g2d.setColor(Color.GREEN);
		}

		g2d.fillRect(0, 0, TILE_SIZE, TILE_SIZE); // Fill the image with the color
		g2d.dispose();
		return image;
	}


	public Main() {
		GraphicsEnvironment ge = null;
		Font a = null;

		try{
			ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			InputStream is = getClass().getClassLoader().getResourceAsStream("fonts/JetBrainsMono-Regular.ttf");
			a = Font.createFont(Font.TRUETYPE_FONT, is);
			ge.registerFont(a);
		} catch(FontFormatException e){} catch (IOException e){
			e.printStackTrace();
		}


		Font font = new Font("JetBrains Mono Regular", Font.PLAIN, 13);

		for (Font name : ge.getAllFonts()) {
			System.out.println(name.getName());
		}

		StyleContext styleContext = new StyleContext();
		Style defaultStyle = styleContext.getStyle(StyleContext.DEFAULT_STYLE);
		Style cwStyle = styleContext.addStyle("ConstantWidth", null);
		StyleConstants.setForeground(cwStyle, new Color(0x729fcf));
		StyleConstants.setLineSpacing(defaultStyle, 1.2f);
		StyleConstants.setLineSpacing(cwStyle, 1.2f);

//		StyleConstants.setFontFamily(cwStyle, font.getFamily());
//		StyleConstants.setFontFamily(defaultStyle, font.getFamily());
//		StyleConstants.setBold(cwStyle, true);

		setTitle("Game Board App");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		JPanel gameBoardPanel = new JPanel();
		gameBoardPanel.setLayout(new GridLayout(1, 1));
//		gameBoardPanel.setLayout();
		gameBoardPanel.setPreferredSize(new Dimension(BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE));

//		StyledEditorKit kit = new StyledEditorKit();


//		JEditorPane jp = new JEditorPane();
////		jp.setEditorKit();
////		jp.setAutoscrolls(true);
//		setLayout(new BorderLayout());
////		jp.setMargin(new Insets(5, 10, 5, 10));
////		gameBoardPanel.add(jp, BorderLayout.CENTER);
//
//		JScrollPane scrollPane = new JScrollPane(jp);
//		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//		gameBoardPanel.add(scrollPane, BorderLayout.CENTER);

		KeywordStyledDocument styled = new KeywordStyledDocument(defaultStyle, cwStyle);

		try {
			styled.insertString(0, """
			Hello World! 
			
			SELECT * FROM Examples WHERE Ratify = TRUE;
			
			Begin
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			 .
			End
			""", null);
		} catch (Throwable ignored) {

		}

		final JTextPane pane = new JTextPane(styled);
		pane.setFont(font);

		JScrollPane scrollPane = new ModernScrollPane(pane);
		scrollPane.getVerticalScrollBar().setUnitIncrement(13 * 2);
//		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		gameBoardPanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(gameBoardPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

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
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch( Exception ex ) {
			System.err.println( "Failed to initialize theme. Using fallback." );
		}

		SwingUtilities.invokeLater(() -> {
			Main app = new Main();
			app.setVisible(true);
		});
	}

}