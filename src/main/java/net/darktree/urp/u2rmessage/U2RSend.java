package net.darktree.urp.u2rmessage;

import net.darktree.urp.NetUtils;

import java.io.DataOutputStream;

public class U2RSend implements U2RMessage {
    private final int toUid;
    private final byte[] data;

    public U2RSend(int toUid, byte[] data) {
        this.toUid = toUid;
        this.data = data;
    }

    public U2RSend(int toUid, String data) {
        this.toUid = toUid;
        this.data = data.getBytes();
    }

    public int getToUid() {
        return toUid;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public void send(DataOutputStream dataOut) {
        try {
            dataOut.writeByte(0x04);
            NetUtils.writeIntLE(dataOut, toUid);
            NetUtils.writeIntLE(dataOut, data.length);
            dataOut.write(data);
        } catch (Exception e) {
            System.out.println("Failed to send message");
        }
    }

    @Override
    public String toString() {
        return "U2RSend{ toUid=" + toUid + ", data=" + new String(data) + '}';
    }
}
