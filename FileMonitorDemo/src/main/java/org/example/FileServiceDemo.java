package org.example;


import org.example.discovery.FileListener;
import org.example.discovery.FileMonitor;
import org.example.distributor.DistributorImpl;
import org.example.distributor.DistributorService;

import java.io.IOException;

public class FileServiceDemo {

    public static void main(String[] args) throws IOException {
        // 这里的监听必须是目录
        String path = "/Users/lifenghua/study/sourcecode/FileMonitorDemo/src/main/resources/13009";
        DistributorService distributorService = new DistributorImpl();
        FileMonitor fileMonitor = new FileMonitor(2000L);
        fileMonitor.monitor(path, new FileListener(distributorService));
        try {
            fileMonitor.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

