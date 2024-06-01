package net.darktree;

import net.darktree.component.HintedTextField;

import javax.swing.*;
import java.awt.*;

public class SharingPopup extends JFrame {

	private final String CONFIRM_TEXT = "Are you sure you want to start sharing?\nAnyone with a access code will be able to view and modify your work.";
	private final Color INVALID = new Color(255, 150, 150);
	private final SelectionCallback callback;
	private final MainWindow parent;

	public interface SelectionCallback {
		void select(SharingAction type, RelayIdentifier relay, String code);
	}

	public SharingPopup(SharingAction type, MainWindow parent, SelectionCallback callback) {
		this.callback = callback;
		this.parent = parent;

		setTitle("Sharing Options");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		if (parent.getStatus().isConnected()) {

			setPreferredSize(new Dimension(250, 164));
			add(getStatusPanel());

		} else {

			setPreferredSize(new Dimension(200, 164));
			JTabbedPane tabs = new JTabbedPane();

			tabs.addTab(" Join ", createTabContent(SharingAction.JOIN));
			tabs.addTab(" Host ", createTabContent(SharingAction.HOST));
			tabs.setSelectedIndex(type == SharingAction.JOIN ? 0 : 1);

			add(tabs);

		}

		pack();
		setLocationRelativeTo(parent);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createTabContent(SharingAction type) {
		JPanel panel = new JPanel(new GridLayout(2, 1));

		JPanel firstRow = (JPanel) panel.add(new JPanel());
		JPanel secondRow = (JPanel) panel.add(new JPanel(new GridLayout(1, 1)));

		JTextField code = new HintedTextField("Join Code");
		JButton button = new JButton(type.getLabel());
		JTextField address = new HintedTextField("localhost:9686", "User Relay Address");

		button.addActionListener(e -> {
			// TODO ugly, replace later
			if (type == SharingAction.HOST && JOptionPane.showInternalConfirmDialog(null, CONFIRM_TEXT, "Start Sharing?", JOptionPane.YES_NO_OPTION) != 0) {
				return;
			}

			callback.select(type, RelayIdentifier.tryParse(address.getText()).orElseThrow(), code.getText());
			dispose();
		});

		// server address verification
		address.addCaretListener(e -> {
			boolean valid = RelayIdentifier.tryParse(address.getText()).isPresent();

			address.setForeground(valid ? null : INVALID);
			button.setEnabled(valid);
		});

		// the code input should be visible only when trying to join
		if (type == SharingAction.JOIN) {
			firstRow.setLayout(new GridLayout(1, 2));
			firstRow.add(code);
		} else {
			firstRow.setLayout(new GridLayout(1, 1));
		}

		firstRow.add(button);
		secondRow.add(address);

		firstRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		secondRow.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		return panel;
	}

	private JPanel getStatusPanel() {

		JPanel panel = new JPanel(new GridLayout(3, 1));

		JTextField groupValue = new JTextField(parent.getGroupJoinCode());
		groupValue.setEditable(false);
		JPanel group = new JPanel(new GridLayout(1, 2));
		group.add(new JLabel("Group:"));
		group.add(groupValue);

		JTextField serverValue = new JTextField(parent.getServerAddress());
		serverValue.setEditable(false);
		JPanel server = new JPanel(new GridLayout(1, 2));
		server.add(new JLabel("Server:"));
		server.add(serverValue);

		JButton leave = new JButton("Leave");

		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(group);
		panel.add(server);
		panel.add(leave);

		leave.addActionListener(e -> {
			callback.select(SharingAction.LEAVE, null, null);
			dispose();
		});

		return panel;

	}

}
