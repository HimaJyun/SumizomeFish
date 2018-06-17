package jp.jyn.sumizomefish.db;

import java.util.List;

public interface DB {

    PageInfo getPageInfo(String url, long unixtime);

    void addPageInfo(String url, long unixtime, PageInfo info);

    List<Long> getHistory(String url);

    long searchLatest(String url);

    final class PageInfo {
        public final boolean redirect;
        // if redirect true : Redirect destination
        // if redirect false: MIMEType
        public final String data;

        public PageInfo(boolean redirect, String data) {
            this.redirect = redirect;
            this.data = data;
        }
    }
}
