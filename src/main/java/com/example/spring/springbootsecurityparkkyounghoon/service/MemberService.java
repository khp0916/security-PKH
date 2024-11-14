package com.example.spring.springbootsecurityparkkyounghoon.service;

import com.example.spring.springbootsecurityparkkyounghoon.config.jwt.TokenProvider;
import com.example.spring.springbootsecurityparkkyounghoon.config.security.CustomUserDetails;
import com.example.spring.springbootsecurityparkkyounghoon.dto.SignInResponseDTO;
import com.example.spring.springbootsecurityparkkyounghoon.enums.Role;
import com.example.spring.springbootsecurityparkkyounghoon.mapper.MemberMapper;
import com.example.spring.springbootsecurityparkkyounghoon.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    public void signUp(Member member) {
        if ("admin".equalsIgnoreCase(member.getUserId())) {
            member.setRole(Role.ROLE_ADMIN);
        } else {
            member.setRole(Role.ROLE_USER);
        }
        memberMapper.signUp(member);
    }

    public SignInResponseDTO signIn(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Member member = ((CustomUserDetails) authentication.getPrincipal()).getMember();

        // Access Token
        String accessToken = tokenProvider.generateToken(member, Duration.ofHours(2));

        // Refresh Token
        String refreshToken = tokenProvider.generateToken(member, Duration.ofDays(2));

        return SignInResponseDTO.builder()
                .isLoggedIn(true)
                .message("로그인 성공")
                .url("/")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(member.getUserId())
                .userName(member.getUserName())
                .build();
    }

}
