package jp.jyn.sumizomefish.view;

import jp.jyn.sumizomefish.db.DB;
import jp.jyn.sumizomefish.manager.DBManager;
import jp.jyn.sumizomefish.manager.DataPathManager;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Controller
@RequestMapping("/view/")
public class ViewController {
    private final DB db;

    public ViewController() {
        db = DBManager.getInstance().getDb();
    }

    @GetMapping("/latest/**")
    public ResponseEntity latest(HttpServletRequest request) {
        String url = searchSubstring(getRequestFullURI(request), "/latest/");
        long latest = db.searchLatest(url);
        if (latest == -1) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, "/page/" + latest + "/" + url)
            .build();
    }

    @GetMapping("/page/{time}/**")
    public String page() {

        return "";
    }

    @GetMapping("/resource/{time}/**")
    @ResponseBody
    public String resource() {
        return null;
    }

    /**
     * Secret API
     */
    @GetMapping("/raw/{time}/**")
    public ResponseEntity<InputStreamResource> raw(HttpServletRequest request, @PathVariable("time") long time) throws IOException {
        String url = searchSubstring(getRequestFullURI(request), "/page/" + time + "/");
        DB.PageInfo info = db.getPageInfo(url, time);

        if (info == null) {
            return ResponseEntity.notFound().build();
        }

        if (info.redirect) {
            return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "/raw/" + time + "/" + info.data)
                .build();
        }

        // TODO: X-Sendfile/X-Accel-Redirect
        Path file = DataPathManager.getDataPath(url, time);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(info.data))
            .contentLength(Files.size(file))
            .body(new InputStreamResource(Files.newInputStream(file)));
    }

    @GetMapping("/list/**")
    @ResponseBody
    public List<Long> list(HttpServletRequest request) {
        String url = searchSubstring(getRequestFullURI(request), "/list/");
        return db.getHistory(url);
    }

    @GetMapping({"/"})
    public String redirect() {
        return "redirect:/";
    }

    private String getRequestFullURI(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        if (query != null) {
            uri += "?" + query;
        }
        return uri;
    }

    private String searchSubstring(String str, String separator) {
        int index = str.indexOf(separator);
        if (index == -1) {
            return str;
        }
        index += separator.length();

        return str.substring(index);
    }
}
