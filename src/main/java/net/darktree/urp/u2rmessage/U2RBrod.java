package net.darktree.urp.u2rmessage;

import net.darktree.urp.NetUtils;

import java.io.DataOutput;
import java.io.DataOutputStream;

public class U2RBrod implements U2RMessage {
	private final int exclude;
	private final byte[] data;

	public U2RBrod(byte[] data, int exclude) {
		this.exclude = exclude;
		this.data = data;
	}

	public U2RBrod(String data, int exclude) {
		this.exclude = exclude;
		this.data = data.getBytes();
	}

	public int getExclude() {
		return exclude;
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public void send(DataOutput buffer){
		try {
			buffer.writeByte(0x03);
			buffer.writeInt(exclude);
			buffer.writeInt(data.length);
			buffer.write(data);
		} catch (Exception e) {
			System.out.println("Failed to send brod request");
		}
	}

	@Override
	public String toString() {
		return "U2RBrod{ exclude=" + exclude + ", data=" + new String(data) + '}';
	}
}
