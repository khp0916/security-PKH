package com.example.spring.springbootsecurityparkkyounghoon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BoardController {

    @GetMapping("/")
    public String board() {
        return "board";
    }

}
