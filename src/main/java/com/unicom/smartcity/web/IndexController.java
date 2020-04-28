package com.unicom.smartcity.web;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {


    @GetMapping("/index")
    public String index() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "index";
    }


}
