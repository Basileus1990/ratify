package net.darktree.urp.u2rmessage;

import net.darktree.urp.NetUtils;

import java.io.DataOutputStream;

public class U2RJoin implements U2RMessage {
    public final int gid;
    public final int pass;

    public U2RJoin(int gid, int pass){
        this.gid = gid;
        this.pass = pass;
    }

    @Override
    public void send(DataOutputStream dataOut){
        try {
            dataOut.writeByte(0x01);
            NetUtils.writeIntLE(dataOut, gid);
            NetUtils.writeIntLE(dataOut, pass);
        } catch (Exception e) {
            System.out.println("Failed to send join request");
        }
    }

    @Override
    public String toString() {
        return "U2RJoin{ gid=" + gid + ", pass=" + pass + '}';
    }
}
