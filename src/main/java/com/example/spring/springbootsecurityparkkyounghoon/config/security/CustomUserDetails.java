package com.example.spring.springbootsecurityparkkyounghoon.config.security;

import com.example.spring.springbootsecurityparkkyounghoon.model.Member;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// CustomUserDetails 클래스는 Spring Security의 UserDetails 인터페이스를 구현하여 사용자 정보를 담는 역할을 합니다.
@Getter
@Builder
public class CustomUserDetails implements UserDetails {
    // Member 객체를 포함하여 사용자의 정보를 저장합니다.
    private Member member;
    private List<String> roles;

    // 사용자의 권한을 반환하는 메서드입니다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // 사용자의 비밀번호를 반환하는 메서드입니다.
    @Override
    public String getPassword() {
        return member.getPassword(); // Member에서 비밀번호를 가져옵니다.
    }

    // 사용자의 아이디를 반환하는 메서드입니다.
    @Override
    public String getUsername() {
        return member.getUserId(); // Member에서 사용자 아이디를 가져옵니다.
    }

    // 계정이 만료되지 않았는지 확인하는 메서드입니다.
    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정은 만료되지 않았습니다.
    }

    // 계정이 잠겨 있지 않은지 확인하는 메서드입니다.
    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정은 잠겨 있지 않습니다.
    }

    // 인증 정보가 만료되지 않았는지 확인하는 메서드입니다.
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 인증 정보는 만료되지 않았습니다.
    }

    // 사용자가 활성화되어 있는지 확인하는 메서드입니다.
    @Override
    public boolean isEnabled() {
        return true; // 사용자는 활성화 상태입니다.
    }
}