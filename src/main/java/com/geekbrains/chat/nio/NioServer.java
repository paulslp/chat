package com.geekbrains.chat.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ByteBuffer buf;

    public NioServer(int port) throws IOException {

        buf = ByteBuffer.allocate(5);
        serverChannel = ServerSocketChannel.open();
        selector = Selector.open();
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

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
                        handleRead(key);
                    }
                    iterator.remove();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
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
        processMessage(channel, msg.toString());
        // String response = "Hello " + msg + key.attachment();
        // channel.write(ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8)));
    }

    private void processMessage(SocketChannel channel, String msg) {
        // TODO: 29.11.2021
    }

    private void handleAccept() throws IOException {
        System.out.println("Client accepted...");
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ, "Hello world!");
    }

    public static void main(String[] args) throws IOException {
        new NioServer(8189);
    }

}
