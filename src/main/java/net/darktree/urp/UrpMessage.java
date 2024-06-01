package net.darktree.urp;

public class UrpMessage {

	public static final byte R2U_WELC = 0x10;
	public static final byte R2U_TEXT = 0x11;
	public static final byte R2U_MADE = 0x12;
	public static final byte R2U_JOIN = 0x13;
	public static final byte R2U_LEFT = 0x14;
	public static final byte R2U_STAT = 0x15;
	public static final byte R2U_VALS = 0x16;

	public static final byte U2R_BROD = 0x03;
	public static final byte U2R_GETS = 0x05;
	public static final byte U2R_JOIN = 0x01;
	public static final byte U2R_KICK = 0x07;
	public static final byte U2R_MAKE = 0x00;
	public static final byte U2R_QUIT = 0x02;
	public static final byte U2R_SEND = 0x04;
	public static final byte U2R_SETS = 0x06;

	private final int type;
	private final int uid;
	private final byte[] data;

	public UrpMessage(int type, int uid, byte[] data) {
		this.uid = uid;
		this.data = data;
		this.type = type;
	}

	public int getSender() {
		return uid;
	}

	public byte[] getData() {
		return data;
	}

	public int getType() {
		return type;
	}

	@Override
	public String toString() {
		return "R2UMessage{ fromUid=" + uid + ", data=" + new String(data) + '}';
	}

}
