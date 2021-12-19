package com.geekbrains.chat.nio;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class Examples {


    public static void main(String[] args) throws IOException {

        Path path = Paths.get("server");
        // path.resolve("ava.jpg");
        WatchService watchService = FileSystems.getDefault().newWatchService();
        new Thread(() -> {
            try {
                while (true) {
                    WatchKey watchKey = watchService.take();
                    System.out.println("New key");
                    List<WatchEvent<?>> events = watchKey.pollEvents();
                    System.out.println("New events");
                    for (WatchEvent<?> event : events) {
                        System.out.println(event.kind() + " " + event.context());
                    }
                    watchKey.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

        // create - dir, file
        // Files.createFile()
        // Files.createDirectory()

        // read
//        Files.readAllBytes();
//        Files.readAllLines();
//        Files.newInputStream();
//        Files.newBufferedReader();

        // write
//        Files.write();
//        Files.newBufferedWriter()

        // Files.list() - список всех фалов и директорий в папке
    }
}
