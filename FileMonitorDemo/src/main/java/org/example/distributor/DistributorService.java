package org.example.distributor;

import java.io.File;
import java.util.List;

public interface DistributorService {
    void send(File file);
    void send(List<File> files);
}
