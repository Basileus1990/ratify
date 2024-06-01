package net.darktree.urp;

import net.darktree.urp.u2rmessage.U2RMessage;

import java.io.DataOutputStream;
import java.util.LinkedList;

public class U2RBuffer {
    private LinkedList<U2RMessage> messages = new LinkedList<>();
    private Thread worker;

    public U2RBuffer(DataOutputStream dataOut){
        worker = new Thread(() -> {
            while (true){
                U2RMessage message = null;
                synchronized (messages){
                    while (messages.size() == 0){
                        try {
                            messages.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    message = messages.poll();
                }
                if (message != null) {
                    message.send(dataOut);
                }
            }
        });
        worker.start();
    }

    public void close(){
        worker.interrupt();
    }

    public void flush(){
        while (true){
            synchronized (messages){
                if (messages.size() <= 0){
                    break;
                }
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }

    public void send(U2RMessage message, boolean wait){
        synchronized (messages){
            messages.add(message);
            messages.notify();
        }

        if (wait){
            flush();
        }
    }
}
