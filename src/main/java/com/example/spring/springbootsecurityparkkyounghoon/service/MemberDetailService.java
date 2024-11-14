package com.example.spring.springbootsecurityparkkyounghoon.service;

import com.example.spring.springbootsecurityparkkyounghoon.config.security.CustomUserDetails;
import com.example.spring.springbootsecurityparkkyounghoon.mapper.MemberMapper;
import com.example.spring.springbootsecurityparkkyounghoon.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {

    private final MemberMapper memberMapper;

    // 1. 사용자 이름으로 사용자 정보 로드
    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 2. 데이터베이스에서 사용자 정보 조회
        Member member = memberMapper.findByUserId(username);
        System.out.println("CustomUserDetails " + member);  // 디버깅용 로그 출력

        // 3. 사용자가 존재하지 않으면 예외 발생
        if (member == null) {
            throw new UsernameNotFoundException(username + " not found");
        }

        // 4. CustomUserDetails 객체 생성하여 반환
        return CustomUserDetails.builder()
                .member(member)  // Member 객체 설정
                .roles(List.of(String.valueOf(member.getRole())))  // 사용자 역할 설정
                .build();
    }
}
