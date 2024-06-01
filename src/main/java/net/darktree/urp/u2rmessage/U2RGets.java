package net.darktree.urp.u2rmessage;

import java.io.DataOutput;

public class U2RGets implements U2RMessage {

	private final int key;

	public U2RGets(int key) {
		this.key = key;
	}

	@Override
	public void send(DataOutput buffer){
		try {
			buffer.writeByte(0x05);
			buffer.writeInt(key);
		} catch (Exception e) {
			System.out.println("Failed to send gets request");
		}
	}

	@Override
	public String toString() {
		return "U2RGets{ key=" + key + '}';
	}

}
