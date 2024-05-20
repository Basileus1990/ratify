package net.darktree.urp;

public class R2UMessage {
    private final int fromUid;
    private final byte[] data;

    public R2UMessage(int fromUid, byte[] data) {
        this.fromUid = fromUid;
        this.data = data;
    }

    public int getFromUid() {
        return fromUid;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "R2UMessage{ fromUid=" + fromUid + ", data=" + new String(data) + '}';
    }
}
