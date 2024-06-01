package net.darktree.urp;

public class URPClientHelper {
	protected String roleToString(byte role) {
		switch (role) {
			case 1:
				return "connected";
			case 2:
				return "member";
			case 4:
				return "host";
			default:
				return "<invalid value>";
		}
	}

	public final static int MAKE_SUCCESS = 0x00;
	public final static int JOIN_SUCCESS = 0x10;

	protected String madeCodeToString(byte code, int gid) {
		if (code == 0x00) return "Created group #" + gid;
		if (code == 0x10) return "Joined group #" + gid;

		if ((code & 0xF0) > 0) {
			return "Failed to join the given group!\n";
		} else {
			return "Failed to create group!\n";
		}
	}
}
