package net.darktree;

public interface OnTypedCallback {
	void onTyped(int offset, String text, int length, boolean moveCursor);
}
