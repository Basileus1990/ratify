package net.darktree;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import java.awt.*;

public class CodePanel extends JPanel {

    private final KeywordStyledDocument styledDocument;
    private final JTextPane pane;

    CodePanel(Font font, Style defaultStyle, Style highlightStyle, KeywordStyledDocument styledDocument) {
        setLayout(new GridLayout(1, 1));

        this.styledDocument = styledDocument;

        this.pane = createTextPane(font);

        // scroll bars panel
        JScrollPane scrollPane = new ModernScrollPane(this.pane);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);

        // TODO: Add smooth scrolling
        //scrollPane.getVerticalScrollBar().addMouseWheelListener(new SmoothMouseWheelListener());

        add(scrollPane, BorderLayout.CENTER);
    }

    private JTextPane createTextPane(Font font) {
        // Overridden methods for disabling wrapping text
        JTextPane pane = new JTextPane(styledDocument) {
            public boolean getScrollableTracksViewportWidth() {
                return false;
            }

            public void setSize( Dimension d ) {
                if( d.width < getParent().getSize().width ) {
                    d.width = getParent().getSize().width;
                }
                super.setSize( d );
            }
        };

        pane.setFont(font);
        pane.setCaretColor(Color.WHITE);
        pane.setBorder(BorderFactory.createEmptyBorder(5, 5, ModernScrollPane.SCROLLBAR_SIZE, ModernScrollPane.SCROLLBAR_SIZE));
        return pane;
    }

    public JTextPane getPane() {
        return pane;
    }
}
