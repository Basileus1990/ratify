package net.darktree;

import net.darktree.urp.UrpClient;
import net.darktree.urp.UrpMessage;

public class Host extends Typewriter {
	private final OnGetTextCallback getWholeText;

	public Host(UrpClient client, OnTypedCallback onTyped, OnGetTextCallback getWholeText) {
		super(client, onTyped);
		this.getWholeText = getWholeText;
	}

	@Override
	protected void initialize() {
		this.hostUid = client.getUid();
		System.out.println("Host UID: " + hostUid + " (me)");
	}

	@Override
	public void listen() {
		listener = new Thread(() -> {
			while (!listener.isInterrupted()){
				UrpMessage message = client.getRxBuffer().receive(true);
				if (message.getType() == UrpMessage.R2U_TEXT && message.getData()[0] == 't') {
					// message for typewriter (client)
					update(message, onTyped);
				}
				else if (message.getType() == UrpMessage.R2U_TEXT && message.getData()[0] == 'h') {
					// message for host
					if (new String(message.getData()).startsWith("hw")) {
						// write command
						String content = new String(message.getData()).substring(2);
						String data = "tw" + message.getSender() + " " + content;

						client.getTxBuffer().writeBrod(-1, data.getBytes());
					}
					else if (new String(message.getData()).startsWith("hr")) {
						// remove command
						String content = new String(message.getData()).substring(2);
						String data = "tr" + message.getSender() + " " + content;

						client.getTxBuffer().writeBrod(-1, data.getBytes());
					}
				}
				else if (message.getType() == UrpMessage.R2U_JOIN) {
					// new client joined, send host welcome message
					String wholeText = getWholeText.getText();
					String data = "ti" + wholeText;

					client.getTxBuffer().writeSend(message.getSender(), data.getBytes());
					System.out.println("Sent welcome message to " + message.getSender());
				}
			}
		});
		listener.start();
	}
}
