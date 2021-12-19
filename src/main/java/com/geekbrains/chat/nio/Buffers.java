package com.geekbrains.chat.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Buffers {

    public static void main(String[] args) throws IOException {

        ByteBuffer buf = ByteBuffer.allocate(5);
        buf.put((byte) 'a');
        buf.put((byte) 'b');
        buf.put((byte) 'c');

        buf.flip();

        while (buf.hasRemaining()) {
            byte b = buf.get();
            System.out.println((char) b);
        }
        buf.rewind();

        RandomAccessFile raf = new RandomAccessFile("server/1.txt", "rw");
        FileChannel channel = raf.getChannel();
//        channel.position(5);
//        channel.write(buf);
//        buf.clear();

        buf.clear();
        channel.position(0);
        channel.read(buf);
        buf.flip();

        while (buf.hasRemaining()) {
            byte b = buf.get();
            System.out.println((char) b);
        }
    }
}
