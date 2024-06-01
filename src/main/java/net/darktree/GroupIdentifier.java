package net.darktree;

public class GroupIdentifier {

	private final boolean join;
	private final int code;

	public GroupIdentifier(boolean join, int code) {
		this.join = join;
		this.code = code;
	}

	public static GroupIdentifier join(int code) {
		return new GroupIdentifier(true, code);
	}

	public static GroupIdentifier make() {
		return new GroupIdentifier(false, 0);
	}

	public boolean shouldJoin() {
		return join;
	}

	public boolean shouldMake() {
		return !join;
	}

	public int getCode() {
		return code;
	}

}
