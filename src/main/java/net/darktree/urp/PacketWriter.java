package net.darktree.urp;

import java.io.DataOutput;
import java.io.IOException;

public class PacketWriter {

	private final DataOutput output;

	public PacketWriter(DataOutput output) {
		this.output = output;
	}

	public void writeBrod(int uid, byte[] data) {
		try {
			output.writeByte(UrpMessage.U2R_BROD);
			output.writeInt(uid);
			output.writeInt(data.length);
			output.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeGets(int key) {
		try {
			output.writeByte(UrpMessage.U2R_GETS);
			output.writeInt(key);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeJoin(int gid, int pass) {
		try {
			output.writeByte(UrpMessage.U2R_JOIN);
			output.writeInt(gid);
			output.writeInt(pass);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeKick(int uid) {
		try {
			output.writeByte(UrpMessage.U2R_KICK);
			output.writeInt(uid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeMake() {
		try {
			output.writeByte(UrpMessage.U2R_MAKE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeQuit() {
		try {
			output.writeByte(UrpMessage.U2R_QUIT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeSend(int uid, byte[] data) {
		try {
			output.writeByte(UrpMessage.U2R_SEND);
			output.writeInt(uid);
			output.writeInt(data.length);
			output.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeSets(int key, int value) {
		try {
			output.writeByte(UrpMessage.U2R_SETS);
			output.writeInt(key);
			output.writeInt(value);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
