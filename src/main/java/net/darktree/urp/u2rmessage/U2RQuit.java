package net.darktree.urp.u2rmessage;

import java.io.DataOutput;
import java.io.DataOutputStream;

public class U2RQuit implements U2RMessage {

	@Override
	public void send(DataOutput buffer){
		try {
			buffer.writeByte(0x02);
		} catch (Exception e) {
			System.out.println("Failed to send quit request");
		}
	}

	@Override
	public String toString() {
		return "U2RQuit";
	}

}
