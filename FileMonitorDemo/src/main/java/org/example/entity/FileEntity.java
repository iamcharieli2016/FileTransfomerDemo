package org.example.entity;

import java.io.File;

public class FileEntity {
    private File file;
    private Integer tries;

    public FileEntity(File file, Integer tries) {
        this.file = file;
        this.tries = tries;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Integer getTries() {
        return tries;
    }

    public void setTries(Integer tries) {
        this.tries = tries;
    }
}
