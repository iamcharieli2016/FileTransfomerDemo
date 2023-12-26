package org.example.discovery.watch;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class FileWatchedService {

    private WatchService watchService;

    private FileWatchedListener listener;

    /**
     *
     * @param path 要监听的目录，注意该 Path 只能是目录，否则会报错 java.nio.file.NotDirectoryException:
     * @param listener 自定义的 listener，用来处理监听到的创建、修改、删除事件
     * @throws IOException
     */
    public FileWatchedService(Path path, FileWatchedListener listener) throws IOException {
        watchService = FileSystems.getDefault().newWatchService();
        path.register(watchService,
                /// 监听文件创建事件
                StandardWatchEventKinds.ENTRY_CREATE,
                /// 监听文件删除事件
                StandardWatchEventKinds.ENTRY_DELETE,
                /// 监听文件修改事件
                StandardWatchEventKinds.ENTRY_MODIFY);

        this.listener = listener;
    }

    private void watch() throws InterruptedException {
        while (true) {
            WatchKey watchKey = watchService.take();
            List<WatchEvent<?>> watchEventList = watchKey.pollEvents();
            for (WatchEvent<?> watchEvent : watchEventList) {
                WatchEvent.Kind<?> kind = watchEvent.kind();

                WatchEvent<Path> curEvent = (WatchEvent<Path>) watchEvent;
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    listener.onOverflowed(curEvent);
                    continue;
                } else if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    listener.onCreated(curEvent);
                    continue;
                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    listener.onModified(curEvent);
                    continue;
                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    listener.onDeleted(curEvent);
                    continue;
                }
            }

            /**
             * WatchKey 有两个状态：
             * {@link sun.nio.fs.AbstractWatchKey.State.READY ready} 就绪状态：表示可以监听事件
             * {@link sun.nio.fs.AbstractWatchKey.State.SIGNALLED signalled} 有信息状态：表示已经监听到事件，不可以接续监听事件
             * 每次处理完事件后，必须调用 reset 方法重置 watchKey 的状态为 ready，否则 watchKey 无法继续监听事件
             */
            if (!watchKey.reset()) {
                break;
            }

        }
    }

    public static void main(String[] args) {
        try {
            Path path = Paths.get("/Users/lifenghua/study/sourcecode/FileMonitorDemo/src/main/resources");
            FileWatchedService fileWatchedService = new FileWatchedService(path, new FileWatchedAdapter());
            fileWatchedService.watch();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}


