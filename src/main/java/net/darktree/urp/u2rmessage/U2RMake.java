package net.darktree.urp.u2rmessage;

import java.io.DataOutput;

public class U2RMake implements U2RMessage {
	public U2RMake() {

	}

	@Override
	public void send(DataOutput buffer){
		try {
			buffer.writeByte(0x00);
		} catch (Exception e) {
			System.out.println("Failed to send make request");
		}
	}

	@Override
	public String toString() {
		return "U2RMake";
	}
}
