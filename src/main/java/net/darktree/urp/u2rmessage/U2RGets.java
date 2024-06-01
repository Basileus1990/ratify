package net.darktree.urp.u2rmessage;

import net.darktree.urp.NetUtils;

import java.io.DataOutputStream;

public class U2RGets implements U2RMessage {
	private final int key;

	public U2RGets(int key) {
		this.key = key;
	}

	@Override
	public void send(DataOutputStream dataOut){
		try {
			dataOut.writeByte(0x05);
			NetUtils.writeIntLE(dataOut, key);
		} catch (Exception e) {
			System.out.println("Failed to send gets request");
		}
	}

	@Override
	public String toString() {
		return "U2RGets{ key=" + key + '}';
	}
}
