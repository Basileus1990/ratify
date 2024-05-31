package net.darktree;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;

// Represents edited document and its connection to a file
public class KeywordStyledDocument extends DefaultStyledDocument  {

	private final Style defaultStyle;
	private final Style hightlightStyle;
	private final CodePanel codePanel;
	private final String filePath;

	// TODO: set styles from a theme
	private final static Set<String> KEYWORDS = Set.of(
			"CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP",
			"DISTINCT", "EXCEPT", "EXISTS", "FALSE", "FETCH",
			"FOR", "FROM", "FULL", "GROUP", "HAVING", "INNER",
			"INTERSECT", "IS", "JOIN", "LIKE", "LIMIT", "MINUS",
			"NATURAL", "NOT", "NULL", "OFFSET", "ON", "ORDER",
			"PRIMARY", "ROWNUM", "SELECT", "SYSDATE", "SYSTIME",
			"SYSTIMESTAMP", "TODAY", "TRUE", "UNION", "UNIQUE", "WHERE"
	);

	public KeywordStyledDocument(Font font, Style defaultStyle, Style hightlightStyle, String filePath) throws IOException {
		this.defaultStyle =  defaultStyle;
		this.hightlightStyle = hightlightStyle;
		this.codePanel = new CodePanel(font, defaultStyle, hightlightStyle, this);
		this.filePath = filePath;

		// Reading the text from a file
		// Ignoring the error because the offset 0 is always valid
		try {
			insertString(0, Files.readString(Paths.get(filePath)), null);
		} catch (BadLocationException ignored) {}
	}

	public JPanel getPanel() {
		return codePanel;
	}

	public String getFileName() {
		String[] splited = filePath.split("/");
		return splited[splited.length - 1];
	}

	// This method is being invoked every time when user writes in the code panel
	public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
		// super.insertString inserts the string into codePanel. If it isn't called, no new text will appear
		super.insertString(offset, str, a);
		refreshDocument();
	}

	// This method is being invoked every time when user removes in the code panel
	public void remove(int offs, int len) throws BadLocationException {
		// super.insertString removes from codePanel. If it isn't called, no text will be removed
		super.remove(offs, len);
		refreshDocument();
	}

	public void save() throws IOException {
		// Ignoring the error because the offset 0 is always valid
		try {
			Files.write(Paths.get(filePath), getText(0, getLength()).getBytes());
		} catch (BadLocationException ignored) {}
	}

	private synchronized void refreshDocument() throws BadLocationException {
		String text = getText(0, getLength());
		final List<Word> list = processWords(text);

		setCharacterAttributes(0, text.length(), defaultStyle, true);

		for(Word word : list) {
			setCharacterAttributes(word.position, word.word.length(), hightlightStyle, true);
		}

		codePanel.repaint();
	}

	private static List<Word> processWords(String content) {
		content += " ";
		List<Word> words = new ArrayList<>();
		int lastWhitespacePosition = 0;
		StringBuilder word = new StringBuilder();
		char[] data = content.toCharArray();

		for(int index = 0; index < data.length; index ++) {
			char ch = data[index];

			if (!(Character.isLetter(ch) || Character.isDigit(ch) || ch == '_')) {
				lastWhitespacePosition = index;

				if(!word.isEmpty()) {
					if(isReservedWord(word.toString())) {
						words.add(new Word(word.toString(), lastWhitespacePosition - word.length()));
					}

					word = new StringBuilder();
				}
			} else {
				word.append(ch);
			}

		}

		return words;
	}

	private static boolean isReservedWord(String word) {
		return KEYWORDS.contains(word.toUpperCase().trim());
	}

}