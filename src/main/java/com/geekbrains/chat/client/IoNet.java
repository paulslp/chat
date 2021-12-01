package com.geekbrains.chat.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class IoNet implements Closeable {

    private final Callback callback;
    private final Socket socket;
    private final InputStream is;
    private final OutputStream os;
    private final byte[] buf;

    public IoNet(Callback callback,
                 Socket socket) throws IOException {
        this.callback = callback;
        this.socket = socket;
        is = socket.getInputStream();
        os = socket.getOutputStream();
        buf = new byte[8192];
        Thread readThread = new Thread(this::readMessages);
        readThread.setDaemon(true);
        readThread.start();
    }

    public void sendMsg(String msg) throws IOException {
        os.write(msg.getBytes(StandardCharsets.UTF_8));
        os.flush();
    }

    private void readMessages() {
        try {
            while (true) {
                int read = is.read(buf);
                String msg = new String(buf, 0, read).trim();
                callback.onReceive(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        os.close();
        is.close();
        socket.close();
    }
}
