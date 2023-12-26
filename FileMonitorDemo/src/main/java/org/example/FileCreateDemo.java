package org.example;

import java.io.*;
import java.nio.file.Files;

public class FileCreateDemo {
    public static void main(String[] args) {
        File source = new File("/Users/lifenghua/study/sourcecode/FileMonitorDemo/src/main/resources/test1.zip");
        File dest = new File("/Users/lifenghua/study/sourcecode/FileMonitorDemo/src/main/resources/test2.zip");
        File dest1 = new File("/Users/lifenghua/study/sourcecode/FileMonitorDemo/src/main/resources/test5.txt");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    copyFileUsingStream(source, dest);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    copyFileUsingStream(source, dest1);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }).start();

    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        try (InputStream is = Files.newInputStream(source.toPath()); OutputStream os = Files.newOutputStream(dest.toPath())) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }
}
