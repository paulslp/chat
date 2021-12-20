package com.geekbrains.chat.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ByteBuffer buf;
    private Path clientDir;
    private String clientMessage;

    public NioServer(int port) throws IOException {

        buf = ByteBuffer.allocate(5);
        serverChannel = ServerSocketChannel.open();
        selector = Selector.open();
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        clientDir = Paths.get("root");


        while (serverChannel.isOpen()) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            try {
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        handleAccept();
                    }
                    if (key.isReadable()) {
                        handleRead(key, false);
                    }
                    iterator.remove();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRead(SelectionKey key, boolean firstCalling) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        String rootDir = clientDir.toAbsolutePath() + ">";
        if (firstCalling) {
            printStringLineToTerminal(channel, rootDir);
        }
        StringBuilder msg = new StringBuilder();
        while (true) {
            int read = channel.read(buf);
            if (read == -1) {
                channel.close();
                return;
            }
            if (read == 0) {
                break;
            }
            buf.flip();
            while (buf.hasRemaining()) {
                msg.append((char) buf.get());
            }
            buf.clear();
        }
        clientMessage = clientMessage.concat(msg.toString());
        System.out.println("clientMessage = " + clientMessage);
        if (clientMessage.endsWith("\r\n")) {
            processMessage(channel, clientMessage.substring(0, clientMessage.length() - 2));
            String response = clientDir.toAbsolutePath() + ">";
            printStringLineToTerminal(channel, response);
            clientMessage = "";
        }
    }

    private void processMessage(SocketChannel channel, String msg) {
        String clientDirValue = clientDir.toAbsolutePath().toString();
        if ("ls".startsWith(msg)) {
            try {
                Files.list(clientDir).filter(path -> !Files.isDirectory(path))
                        .forEach(path ->
                                printStringLineToTerminal(channel, path.getFileName().toString().concat("\r\n")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (msg.startsWith("cat")) {
            String fileName = msg.replace("cat", "").trim();
            try {
                Files.readAllLines(Paths.get(clientDirValue, fileName))
                        .stream().forEach(line ->
                        printStringLineToTerminal(channel, line.concat("\r\n")));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (msg.startsWith("cd")) {
            String dirName = msg.replace("cd", "").trim();

            if ("..".equals(dirName)) {
                clientDir = Paths.get(clientDirValue.substring(0, clientDirValue.lastIndexOf("\\")));
            } else {
                clientDir = Paths.get(clientDirValue, dirName);
            }
        } else if (msg.startsWith("touch")) {
            try {
                String fileName = msg.replace("touch", "").trim();
                Files.createFile(Paths.get(clientDirValue, fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (msg.startsWith("mkdir")) {
            String dirName = msg.replace("mkdir", "").trim();
            try {
                Files.createDirectory(Paths.get(clientDirValue, dirName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void printStringLineToTerminal(SocketChannel channel, String stringLine) {
        try {
            channel.write(ByteBuffer.wrap(stringLine.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAccept() throws IOException {
        System.out.println("Client accepted...");
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);
        SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
        clientMessage = "";
        handleRead(key, true);
    }

    public static void main(String[] args) throws IOException {
        new NioServer(8189);
    }

}
