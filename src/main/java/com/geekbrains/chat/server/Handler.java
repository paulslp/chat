package com.geekbrains.chat.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Handler implements Runnable {

    private boolean running;
    private final byte[] buf;
    private final InputStream is;
    private final OutputStream os;
    private final Socket socket;

    public Handler(Socket socket) throws IOException {
        running = true;
        buf = new byte[8192];
        this.socket = socket;
        is = socket.getInputStream();
        os = socket.getOutputStream();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running) {
                // вкрутить логику с получением файла от клиента
                int read = is.read(buf);
                String message = new String(buf, 0, read)
                        .trim();
                if (message.equals("quit")) {
                    os.write("Client disconnected\n".getBytes(StandardCharsets.UTF_8));
                    close();
                    break;
                }
                System.out.println("Received: " + message);
                os.write((message + "\n").getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close() throws IOException {
        os.close();
        is.close();
        socket.close();
    }
}
