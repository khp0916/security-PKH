package com.example.spring.springbootsecurityparkkyounghoon.config.jwt;

import com.example.spring.springbootsecurityparkkyounghoon.enums.Role;
import com.example.spring.springbootsecurityparkkyounghoon.model.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtProperties jwtProperties;  // JwtProperties를 주입받아 JWT 관련 설정을 사용

    // 1. 토큰 생성 메서드
    public String generateToken(Member member, Duration expiredAt) {
        Date now = new Date();  // 현재 시간을 가져옴
        return makeToken(
                member,  // JWT 토큰에 포함될 member 객체
                new Date(now.getTime() + expiredAt.toMillis())  // 만료 시간을 계산하여 토큰의 만료 시간을 설정
        );
    }

    // 2. JWT 토큰 만들기
    private String makeToken(Member member, Date expire) {
        Date now = new Date();  // 현재 시간
        return Jwts.builder()  // JWT 토큰을 빌드하기 위한 시작
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)  // JWT 타입을 설정 (기본값은 JWT)
                .setIssuer(jwtProperties.getIssuer())  // 토큰 발급자 정보 설정
                .setIssuedAt(now)  // 토큰 발급 시각 설정
                .setExpiration(expire)  // 토큰 만료 시간 설정
                .setSubject(member.getUserId())  // Subject에 사용자 ID 설정
                .claim("id", member.getId())  // 사용자 ID를 claim에 포함
                .claim("role", member.getRole())  // 사용자의 역할 정보를 claim에 포함
                .claim("userName", member.getUserName())  // 사용자의 이름을 claim에 포함
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)  // 서명 알고리즘 (HS512)과 비밀 키로 서명
                .compact();  // JWT 토큰을 생성하여 반환
    }

    // 3. 토큰 검증
    public int validateToken(String token) {
        try {
            Jwts.parserBuilder()  // 토큰을 파싱하기 위한 파서 빌더 시작
                    .setSigningKey(getSecretKey())  // 비밀 키를 설정하여 서명 검증
                    .build()
                    .parseClaimsJws(token);  // 토큰을 파싱하여 클레임을 추출
            log.info("Token validated");  // 토큰이 유효하다면 로그 출력
            return 1;  // 유효한 토큰
        } catch (ExpiredJwtException e) {  // 토큰이 만료된 경우
            log.info("Token is expired");  // 만료된 토큰에 대해 로그 출력
            return 2;  // 만료된 토큰
        } catch (Exception e) {  // 기타 오류가 발생한 경우
            log.info("Token is not valid");  // 잘못된 토큰에 대해 로그 출력
            return 3;  // 잘못된 토큰
        }
    }

    // 4. 토큰에서 회원 정보 가져오기
    public Member getTokenDetails(String token) {
        Claims claims = getClaims(token);  // 토큰에서 클레임을 추출
        return Member.builder()  // Member 객체를 빌드하여 반환
                .id(claims.get("id", Long.class))  // 클레임에서 ID를 추출하여 설정
                .userId(claims.getSubject())  // 클레임에서 사용자 ID를 추출하여 설정
                .userName(claims.get("userName", String.class))  // 클레임에서 사용자 이름을 추출하여 설정
                .role(Role.valueOf(claims.get("role", String.class)))  // 클레임에서 역할을 추출하여 설정
                .build();  // Member 객체를 생성하여 반환
    }

    // 5. 인증 정보 가져오기
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);  // 토큰에서 클레임을 추출
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(  // 역할을 리스트로 변환하여 권한 설정
                new SimpleGrantedAuthority(String.valueOf(claims.get("role")))  // 클레임에서 역할을 추출하여 SimpleGrantedAuthority 생성
        );
        User user = new User(claims.getSubject(), "", authorities);  // UserDetails 객체 생성 (username만 설정, 비밀번호는 비워둠)
        return new UsernamePasswordAuthenticationToken(user, token, authorities);  // 인증 객체 생성하여 반환
    }

    // 6. 토큰에서 클레임 정보 추출
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()  // 토큰 파서 빌더 시작
                .setSigningKey(getSecretKey())  // 서명 검증을 위한 비밀 키 설정
                .build()
                .parseClaimsJws(token)  // 토큰을 파싱하여 클레임을 추출
                .getBody();  // 클레임의 본문(body) 부분을 반환
    }

    // 7. 비밀 키 가져오기
    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecretKey());  // 비밀 키를 Base64로 디코딩
        return Keys.hmacShaKeyFor(keyBytes);  // HMAC SHA 알고리즘을 위한 SecretKey 객체 생성하여 반환
    }
}
