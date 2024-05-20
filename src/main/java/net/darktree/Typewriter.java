package net.darktree;

import net.darktree.urp.R2UMessage;
import net.darktree.urp.URPClient;
import net.darktree.urp.u2rmessage.U2RBrod;


public class Typewriter {
    private URPClient client;

    public Typewriter(URPClient client) {
        this.client = client;
    }

    public void write(int offset, String text) {
        String command = offset + " " + text;
        client.getTxBuffer().send(new U2RBrod(command, -1), false);
    }

    public void listen(OnTypedCallback callback) {
        while (true){
            R2UMessage message = client.getRxBuffer().receive(true);
            int firstSpace = new String(message.getData()).indexOf(" ");
            int offset = Integer.parseInt(new String(message.getData()).substring(0, firstSpace));
            String text = new String(message.getData()).substring(firstSpace + 1);
            callback.onTyped(offset, text);
        }
    }
}
