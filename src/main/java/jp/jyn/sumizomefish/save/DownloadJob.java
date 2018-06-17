package jp.jyn.sumizomefish.save;

import org.apache.logging.log4j.LogManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadJob {
    private final String url;
    private final ExecutorService pool = ThreadPoolSingleton.instance.pool;

    public DownloadJob(String url) {this.url = url;}

    private static class ThreadPoolSingleton {
        public final static ThreadPoolSingleton instance = new ThreadPoolSingleton();
        private final ExecutorService pool;

        private ThreadPoolSingleton() {
            final int core = Runtime.getRuntime().availableProcessors();
            int size = core;

            try {
                size = Integer.parseInt(System.getProperty("sumizome.download.threadsize", String.valueOf(size)));
                if (size == 0 && core != 1) {
                    size = core / 2;
                }
                if (size < 0) {
                    size = core * -size;
                }
            } catch (NumberFormatException e) {
                LogManager.getLogger(ThreadPoolSingleton.class).catching(e);
            }

            pool = Executors.newWorkStealingPool(size);
        }
    }
}
