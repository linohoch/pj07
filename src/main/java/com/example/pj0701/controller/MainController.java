package com.example.pj0701.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainController {

    @RequestMapping("")
    public String toBoard(){
        return "redirect:/board/list";
    }

    @PreAuthorize("permitAll()")
    @RequestMapping("/main")
    public String toMainPage(){
        return "/index";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/admin")
    public String toAdminPage(){
        return "/admin";
    }
}
