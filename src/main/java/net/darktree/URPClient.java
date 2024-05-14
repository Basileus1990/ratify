package net.darktree;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class URPClient {
    private final int port = 9686;
    private Socket socket = null;
    private DataInputStream dataIn = null;
    private DataOutputStream dataOut = null;
    private Thread serverListenerThread = null;
    boolean connected = false;

    public URPClient(String hostname) {
        // initialize websocket connection
        try {
            socket = new Socket(hostname, port);
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Failed to open socket");
            return;
        }

        System.out.println("Successfully made connection to " + hostname + ":" + port);

        // start listening for server messages
        serverListenerThread = new Thread(this::serverListener);
        serverListenerThread.start();
    }

    public boolean isConnected() {
        return connected;
    }

    public void join(int gid, int pass) {
        try {
            dataOut.writeByte(0x01);
            writeIntLE(gid);
            writeIntLE(pass);
        } catch (Exception e) {
            System.out.println("Failed to send join request");
        }
    }

    private void serverListener() {
        System.out.println("Listening for server messages");

        try {
            while (socket.isConnected()) {
                byte id = 0;
                while (true) {
                    if (dataIn.available() > 0) {
                        id = dataIn.readByte();
                        break;
                    }
                }

                switch (id) {
                    case 0x10:
                        // R2U_WELC
                        int ver = readShortLE();
                        int rev = readShortLE();
                        int uid = readIntLE();

                        byte[] message = new byte[65];
                        dataIn.read(message, 0, 64);
                        message[64] = 0;

                        int messageLength = 0;
                        for (int i = 0; i < 64; i++) {
                            if (message[i] == 0) {
                                messageLength = i;
                                break;
                            }
                        }

                        byte[] messageTrimmed = new byte[messageLength];
                        System.arraycopy(message, 0, messageTrimmed, 0, messageLength);

                        System.out.println("Received URP welcome, using protocol v" + ver + "." + rev + " (uid: " + uid + ")");
                        System.out.println("Server identifies as: " + new String(messageTrimmed));

                        connected = true;
                        break;

                    case 0x15:
                        // R2U_STAT
                        byte sta = dataIn.readByte();
                        System.out.println("Your status changed to: " + roleToString(sta));
                        break;

                    case 0x13:
                        // R2U_JOIN
                        int joinUid = readIntLE();
                        System.out.println("User #" + joinUid + " joined");
                        break;

                    case 0x14:
                        // R2U_LEFT
                        int leftUid = readIntLE();
                        System.out.println("User #" + leftUid + " left");
                        break;

                    case 0x16:
                        // R2U_VALS
                        int key = readIntLE();
                        int val = readIntLE();
                        System.out.println("Setting '" + key + "' is set to '" + val + "'");
                        break;

                    case 0x12:
                        // R2U_MADE
                        byte madeStatus = dataIn.readByte();
                        int madeGid = readIntLE();
                        System.out.println(madeCodeToString(madeStatus, madeGid));
                        break;

                    case 0x11:
                        // R2U_TEXT
                        int textUid = readIntLE();
                        int textLen = readIntLE();

                        byte[] textBuffer = new byte[textLen + 1];
                        dataIn.read(textBuffer, 0, textLen);
                        textBuffer[textLen] = 0;

                        System.out.println("User #" + textUid + " said: '" + new String(textBuffer) + "'");
                        break;

                    default:
                        System.out.println("Unknown message id: " + id);
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            connected = false;
        }
    }

    public void close() {
        try {
            serverListenerThread.interrupt();
            dataIn.close();
            dataOut.close();
            socket.close();
            System.out.println("Closed connection");
        } catch (Exception e) {
            System.out.println("Failed to close");
        }
    }

    private String roleToString(byte role) {
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

    private String madeCodeToString(byte code, int gid) {
        if (code == 0x00) return "Created group #" + gid;
        if (code == 0x10) return "Joined group #" + gid;

        if ((code & 0xF0) > 0) {
            return "Failed to join the given group!\n";
        } else {
            return "Failed to create group!\n";
        }
    }

    private int readIntLE() throws Exception {
        int ch1 = dataIn.read();
        int ch2 = dataIn.read();
        int ch3 = dataIn.read();
        int ch4 = dataIn.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new Exception("EOF");
        return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
    }

    private short readShortLE() throws Exception {
        int ch1 = dataIn.read();
        int ch2 = dataIn.read();
        if ((ch1 | ch2) < 0)
            throw new Exception("EOF");
        return (short)((ch2 << 8) + (ch1 << 0));
    }

    private void writeIntLE(int value) throws Exception {
        dataOut.writeByte((value >> 0) & 0xFF);
        dataOut.writeByte((value >> 8) & 0xFF);
        dataOut.writeByte((value >> 16) & 0xFF);
        dataOut.writeByte((value >> 24) & 0xFF);
    }

    private void writeShortLE(short value) throws Exception {
        dataOut.writeByte((value >> 0) & 0xFF);
        dataOut.writeByte((value >> 8) & 0xFF);
    }
}
