package org.example.service.impl;

import org.example.service.DistributorService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class DistributorImpl implements DistributorService {
    @Override
    public void send(File file) {
        System.out.println("将文件推送到hdfs");
    }

    @Override
    public void send(List<File> files) {

    }
}
