package com.example.spring.springbootsecurityparkkyounghoon.controller;

import com.example.spring.springbootsecurityparkkyounghoon.config.jwt.TokenProvider;
import com.example.spring.springbootsecurityparkkyounghoon.dto.*;
import com.example.spring.springbootsecurityparkkyounghoon.enums.Role;
import com.example.spring.springbootsecurityparkkyounghoon.model.Member;
import com.example.spring.springbootsecurityparkkyounghoon.service.MemberService;
import com.example.spring.springbootsecurityparkkyounghoon.service.TokenService;
import com.example.spring.springbootsecurityparkkyounghoon.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final TokenService tokenService;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/join")
    public ResponseEntity<SignUpResponseDTO> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        Member member = signUpRequestDTO.toMember(bCryptPasswordEncoder);

        memberService.signUp(member);

        return ResponseEntity.ok(
                SignUpResponseDTO.builder()
                        .url("/user/login")
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<SignInResponseDTO> signIn(
            @RequestBody SignInRequestDTO signInRequestDTO,
            HttpServletResponse response
    ) {
        SignInResponseDTO signInResponseDTO = memberService.signIn(signInRequestDTO.getUserId(), signInRequestDTO.getPassword());

        CookieUtil.addCookie(response, "refreshToken", signInResponseDTO.getRefreshToken(), 7 * 24 * 60 * 60);
        signInResponseDTO.setRefreshToken(null);

        return ResponseEntity.ok(signInResponseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDTO> logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, "refreshToken");
        return ResponseEntity.ok(
                LogoutResponseDTO.builder()
                        .message("로그아웃 되었습니다.")
                        .url("/member/login")
                        .build()
        );
    }

    @GetMapping("/user/info")
    public ResponseEntity<UserInfoResponseDTO> getUserInfo(HttpServletRequest request) {
        System.out.println("request ::" + request);
        Member member = (Member) request.getAttribute("member");
        System.out.println("member ::" + member);
        return ResponseEntity.ok(
                UserInfoResponseDTO.builder()
                        .id(member.getId())
                        .userId(member.getUserId())
                        .userName(member.getUserName())
                        .role(member.getRole())
                        .build()
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        RefreshTokenResponseDTO refreshTokenResponseDTO = tokenService.refreshToken(request.getCookies());

        if (refreshTokenResponseDTO.isValidated()) {
            // 새로운 Refresh Token을 쿠키에 저장
            CookieUtil.addCookie(response, "refreshToken", refreshTokenResponseDTO.getRefreshToken(), 7 * 24 * 60 * 60);
            refreshTokenResponseDTO.setRefreshToken(null); // 응답에서 Refresh Token 제거
            return ResponseEntity.ok(refreshTokenResponseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(refreshTokenResponseDTO);
        }
    }
}
