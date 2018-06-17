package jp.jyn.sumizomefish.manager;

import jp.jyn.sumizomefish.db.DB;

public class DBManager {
    private final DB db = null;

    private static class Holder {
        public static final DBManager instance = new DBManager();
    }

    public static DBManager getInstance() {
        return Holder.instance;
    }

    private DBManager() {
    }

    public DB getDb() {
        return db;
    }

    public void init() {

    }

    public void close() {

    }
}
