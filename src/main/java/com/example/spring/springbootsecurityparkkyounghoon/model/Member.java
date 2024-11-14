package com.example.spring.springbootsecurityparkkyounghoon.model;

import com.example.spring.springbootsecurityparkkyounghoon.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class Member {
    private long id;
    private String userId;
    private String password;
    private String userName;
    private Role role;
}
