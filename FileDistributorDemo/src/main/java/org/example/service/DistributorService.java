package org.example.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

public interface DistributorService {
    void send(File file);
    void send(List<File> files);
}
