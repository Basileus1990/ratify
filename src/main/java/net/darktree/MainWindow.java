package net.darktree;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainWindow extends JFrame {

    private Map<String, KeywordStyledDocument> openDocuments = new HashMap<>();

    private final Style defaultStyle;
    private final Style highlightStyle;
    private final Font defaultFont;

    private final JPanel codePanelWrapper;

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
        defaultFont = new Font("JetBrains Mono Regular", Font.PLAIN, 13);

        // default text style
        StyleContext styleContext = new StyleContext();
        defaultStyle = styleContext.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setLineSpacing(defaultStyle, 1.2f);

        // style of highlighted words
        highlightStyle = styleContext.addStyle("ConstantWidth", null);
        StyleConstants.setForeground(highlightStyle, new Color(0x729fcf));
        StyleConstants.setLineSpacing(highlightStyle, 1.2f);

        setTitle("Ratify | Untitled Document");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(windowWidth, windowHeight));

        codePanelWrapper = new JPanel(new BorderLayout());
        add(codePanelWrapper, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    public void OpenFile(String path) throws IOException {
        KeywordStyledDocument displayedDocument;
        if (openDocuments.containsKey(path)) {
            displayedDocument = openDocuments.get(path);
        } else {
            displayedDocument = new KeywordStyledDocument(defaultFont, defaultStyle, highlightStyle, path);
            openDocuments.put(path, displayedDocument);
        }

        codePanelWrapper.removeAll();
        codePanelWrapper.add(displayedDocument.getPanel(), BorderLayout.CENTER);

        setTitle("Ratify | " + displayedDocument.getFileName());
    }
}
