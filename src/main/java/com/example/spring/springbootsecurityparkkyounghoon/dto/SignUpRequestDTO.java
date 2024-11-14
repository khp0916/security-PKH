package com.example.spring.springbootsecurityparkkyounghoon.dto;

import com.example.spring.springbootsecurityparkkyounghoon.enums.Role;
import com.example.spring.springbootsecurityparkkyounghoon.model.Member;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
public class SignUpRequestDTO {
    private String userId;
    private String password;
    private String userName;

    public Member toMember(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return Member.builder()
                .userId(userId)
                .password(bCryptPasswordEncoder.encode(password))
                .userName(userName)
                .role(Role.ROLE_USER) // 기본 역할을 ROLE_USER로 설정
                .build();
    }
}
