package com.geekbrains.chat.client;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

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
        Thread readThread = new Thread(this::readFiles);
        readThread.setDaemon(true);
        readThread.start();
    }

    private void readFiles() {
        try {
            while (true) {
                File file = new File("clientDir");
                callback.onReceive(
                        Arrays.stream(file.list()).map(fileName -> "clientDir/" + fileName)
                                .collect(Collectors.toList())
                );
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

    public void writeFileToClientSocketOutputStream(String filePath) {
        File file = new File(filePath);
        byte[] buffer = new byte[8192];
        long start = Instant.now().toEpochMilli();
        try (FileInputStream fis = new FileInputStream(file)) {
            int read;
            while ((read = fis.read(buffer)) != -1) {
                // buf -> socket
                os.write(buffer, 0, read);
                os.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        long end = Instant.now().toEpochMilli();
        System.out.println("Client time: " + (end - start) + " ms.");
    }
}
