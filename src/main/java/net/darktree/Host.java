package net.darktree;

import net.darktree.urp.R2UMessage;
import net.darktree.urp.URPClient;
import net.darktree.urp.u2rmessage.U2RBrod;
import net.darktree.urp.u2rmessage.U2RSend;

public class Host extends Typewriter {
    private final OnGetTextCallack getWholeText;

    public Host(URPClient client, OnTypedCallback onTyped, OnGetTextCallack getWholeText) {
        super(client, onTyped);
        this.getWholeText = getWholeText;
    }

    @Override
    protected void initialize() {
        this.hostUid = client.getUid();
        System.out.println("Host UID: " + hostUid + " (me)");
    }

    @Override
    public void listen() {
        while (true){
            R2UMessage message = client.getRxBuffer().receive(true);
            if (message.getType() == R2UMessage.R2U.TEXT && message.getData()[0] == 't') {
                // message for typewriter (client)
                update(message, onTyped);
            }
            else if (message.getType() == R2UMessage.R2U.TEXT && message.getData()[0] == 'h') {
                // message for host
                String content = new String(message.getData()).substring(2);
                client.getTxBuffer().send(new U2RBrod("tw" + message.getFromUid() + " " + content, -1), false);
            }
            else if (message.getType() == R2UMessage.R2U.JOIN) {
                // new client joined, send host welcome message
                String wholeText = getWholeText.getText();
                client.getTxBuffer().send(new U2RSend(message.getFromUid(), "ti" + wholeText), false);
            }
        }
    }
}
