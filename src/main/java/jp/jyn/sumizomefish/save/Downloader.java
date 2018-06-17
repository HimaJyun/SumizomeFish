package jp.jyn.sumizomefish.save;

import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Downloader {
    private final static OkHttpClient okhttp = new OkHttpClient.Builder()
        .followRedirects(false)
        .followSslRedirects(false)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build();

    private final Logger logger = LogManager.getLogger(Downloader.class);
    private final String userAgent = null;
    private final Path baseDir = null;

    public String download(String url) { // TODO: Stringで受けた方が良いかも
        logger.debug("download: {}", url);
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("Unsupported protocol.");
        }
        URL ur = null;

        try {
            HttpURLConnection con = (HttpURLConnection) ur.openConnection();
            con.setInstanceFollowRedirects(false); // don't follow redirect
            con.setRequestMethod("GET");
            con.connect();

            switch (con.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_MOVED_PERM: // 301
                case HttpURLConnection.HTTP_MOVED_TEMP: // 302
                case HttpURLConnection.HTTP_SEE_OTHER: // 303
                    // TODO: リダイレクト
                    con.getHeaderField("Location");
                default:
                    // TODO: error
            }

            try (BufferedInputStream in = new BufferedInputStream(con.getInputStream())) {
                Files.copy(in, Paths.get(""), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            // TODO: エラー通知
        }
        return null;// TODO
        // TODO: OKhttpに書き換える
        // TODO: リファラ
    }

    public static class Result {
        int code;
        Optional<String> mime;
        Optional<String> location;
        Optional<String> contents;
    }
}
