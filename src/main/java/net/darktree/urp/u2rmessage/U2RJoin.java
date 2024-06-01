package net.darktree.urp.u2rmessage;

import net.darktree.urp.NetUtils;

import java.io.DataOutput;
import java.io.DataOutputStream;

public class U2RJoin implements U2RMessage {

	public final int gid;
	public final int pass;

	public U2RJoin(int gid, int pass){
		this.gid = gid;
		this.pass = pass;
	}

	@Override
	public void send(DataOutput buffer){
		try {
			buffer.writeByte(0x01);
			buffer.writeInt(gid);
			buffer.writeInt(pass);
		} catch (Exception e) {
			System.out.println("Failed to send join request");
		}
	}

	@Override
	public String toString() {
		return "U2RJoin{ gid=" + gid + ", pass=" + pass + '}';
	}

}
