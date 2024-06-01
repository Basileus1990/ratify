package net.darktree.urp;

import java.util.LinkedList;

public class R2UBuffer {

	private final LinkedList<R2UMessage> messages = new LinkedList<>();

	public void addMessage(R2UMessage message) {
		synchronized (messages){
			messages.add(message);
			messages.notify();
		}
	}

	public synchronized R2UMessage receive(boolean wait){
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

					R2UMessage message = messages.poll();
					if (message != null) {
						return message;
					}
				}
			}
		}
		else{
			synchronized (messages) {
				return messages.poll();
			}
		}
	}

}
