package com.assessment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping(value = {
            "/",
            "/test/**",
            "/admin/**",
            "/result/**"
    })
    public String forwardSpa() {
        return "forward:/index.html";
    }
}
