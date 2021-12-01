package com.geekbrains.chat.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Instant;

public class Example {

    public static void main(String[] args) {
        File file = new File("files/java collections.key");
        byte[] buf = new byte[8192];
        long start = Instant.now().toEpochMilli();
        try (FileInputStream is = new FileInputStream(file)) {
            int read;
            try (FileOutputStream os = new FileOutputStream("files/copy")) {
                while ((read = is.read(buf)) != -1) {
                    // buf -> socket
                    os.write(buf, 0, read);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = Instant.now().toEpochMilli();
        System.out.println("Time: " + (end - start) + " ms.");
    }
}
