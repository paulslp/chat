package com.geekbrains.chat.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.Instant;

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
            long start = Instant.now().toEpochMilli();
            while (running) {
                if (is != null) {
                    int read;
                    File file = new File("serverDir/test" + start);
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        while ((read = is.read(buf)) != -1) {
                            fos.write(buf, 0, read);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    long end = Instant.now().toEpochMilli();
                    System.out.println("Server time: " + (end - start) + " ms.");
                }


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
