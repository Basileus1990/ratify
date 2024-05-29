package net.darktree;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import java.awt.*;

public class CodePanel extends JPanel {

    private final KeywordStyledDocument styledDocument;


    CodePanel(Font font, Style defaultStyle, Style highlightStyle) {
        setLayout(new GridLayout(1, 1));

        styledDocument = new KeywordStyledDocument(defaultStyle, highlightStyle, this);

        JTextPane pane = getTextPane(font);

        // scroll bars panel
        JScrollPane scrollPane = new ModernScrollPane(pane);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);

        // TODO: Add smooth scrolling
        //scrollPane.getVerticalScrollBar().addMouseWheelListener(new SmoothMouseWheelListener());


        // link it all together
        add(scrollPane, BorderLayout.CENTER);
    }

    public void insertString(int offset, String str, AttributeSet attributeSet) throws BadLocationException {
        styledDocument.insertString(offset, str, attributeSet);
    }

    public void remove(int offs, int len) throws BadLocationException {
        styledDocument.remove(offs, len);
    }

    private JTextPane getTextPane(Font font) {
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
}
