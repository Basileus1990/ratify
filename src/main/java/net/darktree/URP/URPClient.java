package net.darktree.URP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class URPClient extends URPClientHelper {
    private final int port = 9686;
    private Socket socket = null;
    private DataInputStream dataIn = null;
    private DataOutputStream dataOut = null;
    private Thread serverListenerThread = null;
    private boolean connected = false;
    private int uid = -1;
    // used as fifo queue
    private LinkedList<URPMessage> messages = new LinkedList<>();

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

    public int getUid() {
        return uid;
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

    public URPMessage receive(boolean wait){
        if (wait) {
            while (true) {
                synchronized (messages) {
                    try {
                        messages.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    URPMessage message = messages.poll();
                    if (message != null) {
                        return message;
                    }
                }
            }
        }
        else{
            synchronized (messages) {
                return messages.poll();
            }
        }
    }

    public void make(){
        try {
            dataOut.writeByte(U2R.MAKE.getValue());
        } catch (Exception e) {
            System.out.println("Failed to send make request");
        }
    }

    public void join(int gid, int pass) {
        try {
            dataOut.writeByte(U2R.JOIN.getValue());
            NetUtils.writeIntLE(dataOut, gid);
            NetUtils.writeIntLE(dataOut, pass);
        } catch (Exception e) {
            System.out.println("Failed to send join request");
        }
    }

    public void quit() {
        try {
            dataOut.writeByte(U2R.QUIT.getValue());
        } catch (Exception e) {
            System.out.println("Failed to send quit request");
        }
    }

    public void broadcast(String message, int exclude) {
        broadcast(message.getBytes(), exclude);
    }

    public void broadcast(byte[] message, int exclude) {
        try {
            dataOut.writeByte(U2R.BROD.getValue());
            NetUtils.writeIntLE(dataOut, exclude);
            NetUtils.writeIntLE(dataOut, message.length);
            dataOut.write(message);
        } catch (Exception e) {
            System.out.println("Failed to send broadcast request");
        }
    }

    public void send(int uid, byte[] message) {
        try {
            dataOut.writeByte(U2R.SEND.getValue());
            NetUtils.writeIntLE(dataOut, uid);
            NetUtils.writeIntLE(dataOut, message.length);
            dataOut.write(message);
        } catch (Exception e) {
            System.out.println("Failed to send message");
        }
    }

    public void getSetting(int key) {
        try {
            dataOut.writeByte(U2R.GETS.getValue());
            NetUtils.writeIntLE(dataOut, key);
        } catch (Exception e) {
            System.out.println("Failed to send getSettings request");
        }
    }

    public void setSetting(int key, int value) {
        try {
            dataOut.writeByte(U2R.SETS.getValue());
            NetUtils.writeIntLE(dataOut, key);
            NetUtils.writeIntLE(dataOut, value);
        } catch (Exception e) {
            System.out.println("Failed to send setSettings request");
        }
    }

    public void kick(int uid) {
        try {
            dataOut.writeByte(U2R.KICK.getValue());
            NetUtils.writeIntLE(dataOut, uid);
        } catch (Exception e) {
            System.out.println("Failed to send kick request");
        }
    }

    private void serverListener() {
        System.out.println("Listening for server messages");

        try {
            while (socket.isConnected() && !serverListenerThread.isInterrupted()) {
                R2U id = null;
                while (socket.isConnected() && !serverListenerThread.isInterrupted()) {
                    if (dataIn.available() > 0) {
                        id = R2U.fromInt(dataIn.readByte());
                        if (id != null){
                            break;
                        }
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
                        connected = true;
                    }
                    case STAT -> {
                        byte sta = dataIn.readByte();
                        System.out.println("Your status changed to: " + roleToString(sta));
                    }
                    case JOIN -> {
                        int joinUid = NetUtils.readIntLE(dataIn);
                        System.out.println("User #" + joinUid + " joined");
                    }
                    case LEFT -> {
                        int leftUid = NetUtils.readIntLE(dataIn);
                        System.out.println("User #" + leftUid + " left");
                    }
                    case VALS -> {
                        int key = NetUtils.readIntLE(dataIn);
                        int val = NetUtils.readIntLE(dataIn);
                        System.out.println("Setting '" + key + "' is set to '" + val + "'");
                    }
                    case MADE -> {
                        byte madeStatus = dataIn.readByte();
                        int madeGid = NetUtils.readIntLE(dataIn);
                        System.out.println(madeCodeToString(madeStatus, madeGid));
                    }
                    case TEXT -> {
                        int textUid = NetUtils.readIntLE(dataIn);
                        int textLen = NetUtils.readIntLE(dataIn);

                        byte[] textBuffer = new byte[textLen + 1];
                        dataIn.read(textBuffer, 0, textLen);
                        textBuffer[textLen] = 0;
                        System.out.println("User #" + textUid + " said: '" + new String(textBuffer) + "'");

                        synchronized (messages) {
                            messages.add(new URPMessage(textUid, textBuffer));
                            messages.notify();
                        }
                    }
                    default -> System.out.println("Unknown message id: " + id);
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
                client.join(gid, pass);
            } else if (line.startsWith("quit")) {
                client.quit();
            } else if (line.equals("make")) {
                client.make();
            } else if (line.startsWith("brod")) {
                String message = line.substring(5);
                client.broadcast(message.getBytes(), client.getUid());
            } else if (line.startsWith("send")) {
                int uid = Integer.parseInt(line.split(" ")[1]);
                String message = line.substring(6 + line.split(" ")[1].length());
                client.send(uid, message.getBytes());
            } else {
                System.out.println("Unknown command");
            }
        }
        client.close();
    }
}
