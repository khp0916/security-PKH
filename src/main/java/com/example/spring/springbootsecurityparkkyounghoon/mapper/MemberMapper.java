package com.example.spring.springbootsecurityparkkyounghoon.mapper;

import com.example.spring.springbootsecurityparkkyounghoon.model.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    void signUp(Member member);
    Member findByUserId(String username);
}
