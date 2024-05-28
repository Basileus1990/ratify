package net.darktree;

import net.darktree.urp.R2UMessage;
import net.darktree.urp.URPClient;
import net.darktree.urp.u2rmessage.U2RBrod;


public class Typewriter {
    protected URPClient client;
    protected int hostUid = -1;
    protected OnTypedCallback onTyped;

    public Typewriter(URPClient client, OnTypedCallback onTyped) {
        this.client = client;
        this.onTyped = onTyped;
        initialize();
    }

    protected void initialize() {
        // wait for initialization message (hello) from host
        while (true) {
            R2UMessage message = client.getRxBuffer().receive(true);
            if (message.getType() == R2UMessage.R2U.TEXT && new String(message.getData()).startsWith("ti")) {
                hostUid = message.getFromUid();
                onTyped.onTyped(0, new String(message.getData()).substring(2), false);
                System.out.println("Host UID: " + hostUid);
                break;
            }
        }
    }

    public void write(int offset, String text) {
        String command = "hw" + offset + " " + text;
        client.getTxBuffer().send(new U2RBrod(command, -1), false);
    }

    protected void update(R2UMessage message, OnTypedCallback callback) {
        int firstSpace = new String(message.getData()).indexOf(" ");
        int typedBy = Integer.parseInt(new String(message.getData()).substring(2, firstSpace));
        int secondSpace = new String(message.getData()).indexOf(" ", firstSpace + 1);
        int offset = Integer.parseInt(new String(message.getData()).substring(firstSpace + 1, secondSpace));

        String text = new String(message.getData()).substring(secondSpace + 1);
        boolean moveCursor = typedBy == client.getUid();
        callback.onTyped(offset, text, moveCursor);
    }

    public void listen() {
        while (true){
            R2UMessage message = client.getRxBuffer().receive(true);
            if (message.getType() == R2UMessage.R2U.TEXT && new String(message.getData()).startsWith("tw")) {
                update(message, onTyped);
            }
        }
    }
}
