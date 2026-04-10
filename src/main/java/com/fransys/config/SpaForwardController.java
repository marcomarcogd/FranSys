package com.fransys.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping({"/", "/login", "/admin"})
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/admin/**")
    public String spaRoutes() {
        return "forward:/index.html";
    }
}
