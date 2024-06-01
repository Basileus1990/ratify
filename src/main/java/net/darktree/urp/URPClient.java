package net.darktree.urp;

import net.darktree.urp.u2rmessage.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class URPClient extends URPClientHelper {
    //private final int port = 9686;
    private Socket socket = null;
    private DataInputStream dataIn = null;
    private DataOutputStream dataOut = null;
    private Thread serverListenerThread = null;
    private boolean connected = false;
    private int uid = -1;
    private R2UBuffer r2u;
    private U2RBuffer u2r;

    public URPClient(String hostname) {
        // initialize websocket connection
        try {
            socket = new Socket(hostname.split(":")[0], hostname.split(":").length > 1 ? Integer.parseInt(hostname.split(":")[1]) : 9686);
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Failed to open socket");
            return;
        }

        System.out.println("Successfully made connection to " + hostname);

        r2u = new R2UBuffer();

        // start listening for server messages
        serverListenerThread = new Thread(this::serverListener);
        serverListenerThread.start();

        u2r = new U2RBuffer(dataOut);
    }

    public boolean isConnected() {
        return connected;
    }

    public int getUid() {
        return uid;
    }

    public R2UBuffer getRxBuffer(){
        return r2u;
    }

    public U2RBuffer getTxBuffer(){
        return u2r;
    }

    public void waitForConnection(int timeout) {
        long start = System.currentTimeMillis();
        while (!connected) {
            try {
                Thread.sleep(10);
                if (System.currentTimeMillis() - start > timeout) {
                    System.out.println("Connection timeout");
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void serverListener() {
        System.out.println("Listening for server messages");

        try {
            while (socket.isConnected() && !serverListenerThread.isInterrupted()) {
                R2UMessage.R2U id = null;
                while (socket.isConnected() && !serverListenerThread.isInterrupted()) {
                    id = R2UMessage.R2U.fromInt(dataIn.readByte());
                    if (id != null){
                        break;
                    }
                }

                if (id == null) {
                    continue;
                }

                switch (id) {
                    case WELC -> {
                        int ver = NetUtils.readShortLE(dataIn);
                        int rev = NetUtils.readShortLE(dataIn);
                        uid = NetUtils.readIntLE(dataIn);

                        byte[] message = new byte[65];
                        dataIn.read(message, 0, 64);
                        message[64] = 0;

                        // trim message to remove null bytes
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

                        r2u.addMessage(new R2UMessage(R2UMessage.R2U.WELC, -1, new byte[]{(byte) ver, (byte) rev}));

                        connected = true;
                    }
                    case STAT -> {
                        byte sta = dataIn.readByte();
                        System.out.println("Your status changed to: " + roleToString(sta));

                        r2u.addMessage(new R2UMessage(R2UMessage.R2U.STAT, -1, new byte[]{sta}));
                    }
                    case JOIN -> {
                        int joinUid = NetUtils.readIntLE(dataIn);
                        System.out.println("User #" + joinUid + " joined");

                        r2u.addMessage(new R2UMessage(R2UMessage.R2U.JOIN, joinUid, new byte[0]));
                    }
                    case LEFT -> {
                        int leftUid = NetUtils.readIntLE(dataIn);
                        System.out.println("User #" + leftUid + " left");

                        r2u.addMessage(new R2UMessage(R2UMessage.R2U.LEFT, leftUid, new byte[0]));
                    }
                    case VALS -> {
                        int key = NetUtils.readIntLE(dataIn);
                        int val = NetUtils.readIntLE(dataIn);
                        System.out.println("Setting '" + key + "' is set to '" + val + "'");

                        byte[] valBuffer = new byte[8];
                        NetUtils.writeIntLE(valBuffer, 0,val);
                        NetUtils.writeIntLE(valBuffer, 4, key);
                        r2u.addMessage(new R2UMessage(R2UMessage.R2U.VALS, -1, valBuffer));
                    }
                    case MADE -> {
                        byte madeStatus = dataIn.readByte();
                        int madeGid = NetUtils.readIntLE(dataIn);
                        System.out.println(madeCodeToString(madeStatus, madeGid));

                        byte[] madeBuffer = new byte[5];
                        NetUtils.writeIntLE(madeBuffer, 0, madeGid);
                        madeBuffer[4] = madeStatus;
                        r2u.addMessage(new R2UMessage(R2UMessage.R2U.MADE, -1, madeBuffer));
                    }
                    case TEXT -> {
                        int textUid = NetUtils.readIntLE(dataIn);
                        int textLen = NetUtils.readIntLE(dataIn);

                        byte[] textBuffer = new byte[textLen + 1];
                        dataIn.read(textBuffer, 0, textLen);
                        textBuffer[textLen] = 0;
                        System.out.println("User #" + textUid + " said: '" + new String(textBuffer) + "'");

                        r2u.addMessage(new R2UMessage(R2UMessage.R2U.TEXT, textUid, textBuffer));
                    }
                    default -> System.out.println("Unknown message id: " + id);
                }

            }
        } catch (Exception e) {
            //e.printStackTrace();
            connected = false;
        }
    }

    public void close() {
        try {
            serverListenerThread.interrupt();
            u2r.close();
            dataIn.close();
            dataOut.close();
            socket.close();
            System.out.println("Closed connection");
        } catch (Exception e) {
            System.out.println("Failed to close");
        }
    }

    public static void interactive() {
        URPClient client = new URPClient("localhost");

        while (!client.isConnected()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Scanner scanner = new Scanner(System.in);
        while (client.isConnected()) {
            String line = scanner.nextLine();
            if (line.equals("exit")) {
                break;
            } else if (line.startsWith("join")) {
                int gid = Integer.parseInt(line.split(" ")[1]);
                int pass = Integer.parseInt(line.split(" ")[2]);
                client.getTxBuffer().send(new U2RJoin(gid, pass), true);
            } else if (line.startsWith("quit")) {
                client.getTxBuffer().send(new U2RQuit(), true);
            } else if (line.equals("make")) {
                client.getTxBuffer().send(new U2RMake(), true);
            } else if (line.startsWith("brod")) {
                String message = line.substring(5);
                client.getTxBuffer().send(new U2RBrod(message, client.getUid()), true);
            } else if (line.startsWith("send")) {
                int uid = Integer.parseInt(line.split(" ")[1]);
                String message = line.substring(6 + line.split(" ")[1].length());
                client.getTxBuffer().send(new U2RSend(uid, message), true);
            } else {
                System.out.println("Unknown command");
            }
        }
        client.close();
    }
}
