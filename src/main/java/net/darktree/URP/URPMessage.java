package net.darktree.URP;

public class URPMessage {
    private final int fromUid;
    private final byte[] data;

    public URPMessage(int fromUid, byte[] data) {
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
        return "URPMessage{" +
                "fromUid=" + fromUid +
                ", data=" + new String(data) +
                '}';
    }
}
