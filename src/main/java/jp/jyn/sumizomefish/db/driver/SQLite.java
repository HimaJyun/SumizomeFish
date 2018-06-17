package jp.jyn.sumizomefish.db.driver;

import com.zaxxer.hikari.HikariDataSource;
import jp.jyn.sumizomefish.db.DB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SQLite implements DB {
    private final Logger logger = LogManager.getLogger(SQLite.class);

    private final Map<String, Map<Long, PageInfo>> cache = new ConcurrentHashMap<>();

    private HikariDataSource hdc;

    // TODO: jdbcTemplate
    @Override
    public void addPageInfo(String url, long unixtime, PageInfo info) {
        try (Connection con = hdc.getConnection();
             PreparedStatement stat = con.prepareStatement("INSERT INTO page VALUES(?,?,?,?)")) {
            stat.setString(1, url);
            stat.setLong(2, unixtime);
            stat.setBoolean(3, info.redirect);
            stat.setString(4, info.data);
            stat.executeUpdate();
        } catch (SQLException e) {
            logger.catching(e);
        }
    }

    @Override
    public PageInfo getPageInfo(String url, long unixtime) {
        PageInfo c = cache.getOrDefault(url, Collections.emptyMap()).get(unixtime);
        if (c != null) {
            return c;
        }
        try (Connection con = hdc.getConnection();
             PreparedStatement stat = con.prepareStatement("SELECT redirect,data FROM page WHERE url=? AND time=?")) {
            stat.setString(1, url);
            stat.setLong(2, unixtime);
            try (ResultSet rs = stat.executeQuery()) {
                if (rs.next()) {
                    c = new PageInfo(rs.getBoolean(1), rs.getString(2));
                    cachePut(url, unixtime, c);
                }
            }
        } catch (SQLException e) {
            logger.catching(e);
        }
        return c;
    }

    @Override
    public List<Long> getHistory(String url) {
        try (Connection con = hdc.getConnection();
             PreparedStatement stat = con.prepareStatement("SELECT time FROM page WHERE url=?")) {
            stat.setString(1, url);
            try (ResultSet rs = stat.executeQuery()) {
                List<Long> r = new ArrayList<>();
                while (rs.next()) {
                    r.add(rs.getLong(1));
                }
                return r;
            }
        } catch (SQLException e) {
            logger.catching(e);
        }
        return null;
    }

    @Override
    public long searchLatest(String url) {
        try (Connection con = hdc.getConnection();
             PreparedStatement stat = con.prepareStatement("SELECT time FROM page WHERE url=? ORDER BY time DESC LIMIT 1")) {
            stat.setString(1, url);
            try (ResultSet rs = stat.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            logger.catching(e);
        }
        return -1;
    }

    private void cachePut(String url, long unixtime, PageInfo info) {
        cache.computeIfAbsent(url, i -> new ConcurrentHashMap<>()).putIfAbsent(unixtime, info);
    }
}
