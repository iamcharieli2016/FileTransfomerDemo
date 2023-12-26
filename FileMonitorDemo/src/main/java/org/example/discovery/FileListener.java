package org.example.discovery;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.example.distributor.DistributorService;

import java.io.File;

public class FileListener extends FileAlterationListenerAdaptor {

    private DistributorService distributorService;

    public FileListener(DistributorService distributorService) {
        this.distributorService = distributorService;
    }

    @Override
    public void onStart(FileAlterationObserver observer) {
        super.onStart(observer);
        // System.out.println("一轮轮询开始，被监视路径：" + observer.getDirectory());
    }

    @Override
    public void onDirectoryCreate(File directory) {
        System.out.println("创建文件夹：" + directory.getAbsolutePath());
    }

    @Override
    public void onDirectoryChange(File directory) {
        System.out.println("修改文件夹：" + directory.getAbsolutePath());
    }

    @Override
    public void onDirectoryDelete(File directory) {
        System.out.println("删除文件夹：" + directory.getAbsolutePath());
    }

    @Override
    public void onFileCreate(File file) {
        String compressedPath = file.getAbsolutePath();
        System.out.println("新建文件：" + compressedPath);

        if (file.canRead()) {
            // TODO 读取或重新加载文件内容
            System.out.println("文件变更，进行处理");
            System.out.println("文件长度： " + file.length());
            distributorService.send(file);
        }
    }

    @Override
    public void onFileChange(File file) {
        String compressedPath = file.getAbsolutePath();
        System.out.println("修改文件：" + compressedPath);
    }

    @Override
    public void onFileDelete(File file) {
        System.out.println("删除文件：" + file.getAbsolutePath());
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
        super.onStop(observer);
        // System.out.println("一轮轮询结束，被监视路径：" + fileAlterationObserver.getDirectory());
    }
}

