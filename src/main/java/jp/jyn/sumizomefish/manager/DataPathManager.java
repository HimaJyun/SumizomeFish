package jp.jyn.sumizomefish.manager;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DataPathManager {
    // Path is thread safe?
    private final static ThreadLocal<Path> basePath = ThreadLocal.withInitial(() -> Paths.get(System.getProperty("sumizome.datadir", "./data")));

    public static Path getDataPath(String url) {
        String hash = DigestUtils.sha256Hex(url);
        return basePath.get().resolve(hash.charAt(0) + "/" + hash.substring(1, 3) + "/" + hash);
    }

    public static Path getDataPath(String url, long time) {
        return getDataPath(url).resolve(String.valueOf(time));
    }
}
