package jp.jyn.sumizomefish.save;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/save/")
@SessionAttributes("job")
public class SaveController {
    private final Logger logger = LogManager.getLogger(SaveController.class);

    @ModelAttribute("job")
    public Map<String, DownloadJob> job() {
        return new ConcurrentHashMap<>();
    }

    @GetMapping("/run")
    public String save(@ModelAttribute("job") Map<String, DownloadJob> session,
                       @RequestParam("url") String url,
                       @RequestParam(value = "crawl", required = false) boolean crawl) {
        long unixtime = System.currentTimeMillis() / 1000L;
        for (String s : url.replace("\r", "").split("\n")) {
            session.put(s, new DownloadJob(s));
        }

        return "index";
    }

    @GetMapping("/progress")
    @ResponseBody
    public Map<String, Map<String, Integer>> progress(@ModelAttribute("job") Map<String, DownloadJob> session,
                                                      SessionStatus sessionStatus) {
        return null;
    }

    @GetMapping({"/"})
    public String redirect() {
        return "redirect:/";
    }
}
