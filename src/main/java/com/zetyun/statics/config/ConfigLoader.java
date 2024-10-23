package com.zetyun.statics.config;

import java.io.FileInputStream;
import java.util.Properties;
public class ConfigLoader {
    private static Properties properties = new Properties();

    public ConfigLoader(String path) throws Exception {
        try(FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
