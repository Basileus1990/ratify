package net.darktree;

public interface OnTypedCallback {
    public void onTyped(int offset, String text, int length, boolean moveCursor);
}
