package net.darktree.urp;

import java.util.LinkedList;

public class PacketReader {

	private final LinkedList<UrpMessage> messages = new LinkedList<>();

	public void addMessage(UrpMessage message) {
		synchronized (messages){
			messages.add(message);
			messages.notify();
		}
	}

	public UrpMessage receive(boolean wait){
		if (wait) {
			while (true) {
				synchronized (messages) {
					if (messages.isEmpty()) {
						try {
							messages.wait();
						} catch (InterruptedException e) {
							//e.printStackTrace();
						}
					}

					UrpMessage message = messages.poll();
					if (message != null) {
						return message;
					}
				}
			}
		} else {
			synchronized (messages) {
				return messages.poll();
			}
		}
	}

}
