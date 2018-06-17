package jp.jyn.sumizomefish;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.file.Paths;

@SpringBootApplication
@Controller
public class Main {
    public static void main(String[] args) {
        ConfigLoader.load(Paths.get("./sumizomefish.conf"));
        SpringApplication.run(Main.class, args);
    }

    @GetMapping("/")
    public String root() {
        return "index";
    }
}
