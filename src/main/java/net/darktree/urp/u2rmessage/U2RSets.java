package net.darktree.urp.u2rmessage;

import net.darktree.urp.NetUtils;

import java.io.DataOutputStream;

public class U2RSets implements U2RMessage {
	private final int key;
	private final int value;

	public U2RSets(int key, int value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public void send(DataOutputStream dataOut){
		try {
			dataOut.writeByte(0x06);
			NetUtils.writeIntLE(dataOut, key);
			NetUtils.writeIntLE(dataOut, value);
		} catch (Exception e) {
			System.out.println("Failed to send sets request");
		}
	}

	@Override
	public String toString() {
		return "U2RSets{ key=" + key + ", value=" + value + '}';
	}
}
