package org.example.distributor;

import org.example.util.HttpUtils;

import java.io.File;
import java.util.List;

public class DistributorImpl implements DistributorService {
    @Override
    public void send(File file) {
        try {
            HttpUtils.httpPost(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(List<File> files) {

    }
}
