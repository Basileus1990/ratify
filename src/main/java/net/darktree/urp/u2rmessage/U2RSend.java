package net.darktree.urp.u2rmessage;

import net.darktree.urp.NetUtils;

import java.io.DataOutput;
import java.io.DataOutputStream;

public class U2RSend implements U2RMessage {
	private final int toUid;
	private final byte[] data;

	public U2RSend(int toUid, byte[] data) {
		this.toUid = toUid;
		this.data = data;
	}

	public U2RSend(int toUid, String data) {
		this.toUid = toUid;
		this.data = data.getBytes();
	}

	public int getToUid() {
		return toUid;
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public void send(DataOutput buffer) {
		try {
			buffer.writeByte(0x04);
			buffer.writeInt(toUid);
			buffer.writeInt(data.length);
			buffer.write(data);
		} catch (Exception e) {
			System.out.println("Failed to send message");
		}
	}

	@Override
	public String toString() {
		return "U2RSend{ toUid=" + toUid + ", data=" + new String(data) + '}';
	}
}
