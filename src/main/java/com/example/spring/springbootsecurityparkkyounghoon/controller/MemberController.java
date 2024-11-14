package com.example.spring.springbootsecurityparkkyounghoon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MemberController {

    @GetMapping("/join") // "/member/join" 경로로 들어오는 GET 요청 처리
    public String signUp() {
        // 회원가입 페이지 뷰 반환
        return "sign-up"; // "sign-up" 뷰 이름 반환
    }

    @GetMapping("/login") // "/member/login" 경로로 들어오는 GET 요청 처리
    public String signIn() {
        // 로그인 페이지 뷰 반환
        return "sign-in"; // "sign-in" 뷰 이름 반환
    }
}
