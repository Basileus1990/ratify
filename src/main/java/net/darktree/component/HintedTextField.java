package net.darktree.component;

import javax.swing.*;
import java.awt.*;

public class HintedTextField extends JTextField {

	private final String placeholder;

	public HintedTextField(String text, String placeholder) {
		super(text);
		this.placeholder = placeholder;
	}

	public HintedTextField(String placeholder) {
		this("", placeholder);
	}

	@Override
	protected void paintComponent(final Graphics graphics) {
		super.paintComponent(graphics);

		if (placeholder == null || !getText().isEmpty()) {
			return;
		}

		graphics.setColor(Color.GRAY);
		graphics.drawString(placeholder, getInsets().left, graphics.getFontMetrics().getMaxAscent() + getInsets().top);
	}

}
