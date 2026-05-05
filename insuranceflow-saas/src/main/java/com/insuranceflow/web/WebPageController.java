package com.insuranceflow.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebPageController {

    @GetMapping("/")
    public String landing() {
        return "landing";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/master")
    public String master() {
        return "master";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/portal")
    public String portal() {
        return "portal";
    }

    @GetMapping("/onboarding")
    public String onboarding() {
        return "onboarding";
    }
}
