package net.darktree;

import javax.swing.*;
import javax.swing.text.Caret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MainWindow extends JFrame {

    public final CodePanel codePanel;

    final int windowWidth = 1200;
    final int windowHeight = 800;

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

    public MainWindow() {
        // default font
        registerLocalFont("fonts/JetBrainsMono-Regular.ttf");
        Font font = new Font("JetBrains Mono Regular", Font.PLAIN, 13);

        // default text style
        StyleContext styleContext = new StyleContext();
        Style defaultStyle = styleContext.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setLineSpacing(defaultStyle, 1.2f);

        // style of highlighted words
        Style highlightStyle = styleContext.addStyle("ConstantWidth", null);
        StyleConstants.setForeground(highlightStyle, new Color(0x729fcf));
        StyleConstants.setLineSpacing(highlightStyle, 1.2f);

        setTitle("Ratify | Untitled Document");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(windowWidth, windowHeight));


        codePanel = new CodePanel(font, defaultStyle, highlightStyle);

        add(codePanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }
}
