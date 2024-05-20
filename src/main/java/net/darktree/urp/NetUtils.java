package net.darktree.urp;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class NetUtils {
    public static int readIntLE(DataInputStream dataIn) throws Exception {
        int ch1 = dataIn.read();
        int ch2 = dataIn.read();
        int ch3 = dataIn.read();
        int ch4 = dataIn.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new Exception("EOF");
        return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
    }

    public static short readShortLE(DataInputStream dataIn) throws Exception {
        int ch1 = dataIn.read();
        int ch2 = dataIn.read();
        if ((ch1 | ch2) < 0)
            throw new Exception("EOF");
        return (short)((ch2 << 8) + (ch1 << 0));
    }

    public static void writeIntLE(DataOutputStream dataOut, int value) throws Exception {
        dataOut.writeByte((value >> 0) & 0xFF);
        dataOut.writeByte((value >> 8) & 0xFF);
        dataOut.writeByte((value >> 16) & 0xFF);
        dataOut.writeByte((value >> 24) & 0xFF);
    }

    public static void writeShortLE(DataOutputStream dataOut, short value) throws Exception {
        dataOut.writeByte((value >> 0) & 0xFF);
        dataOut.writeByte((value >> 8) & 0xFF);
    }
}
