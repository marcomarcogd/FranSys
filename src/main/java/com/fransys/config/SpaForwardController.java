package com.fransys.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping({"/", "/login", "/admin", "/public"})
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping({"/admin/**", "/public/**"})
    public String spaRoutes() {
        return "forward:/index.html";
    }
}
