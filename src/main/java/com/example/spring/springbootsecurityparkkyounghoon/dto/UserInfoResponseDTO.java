package com.example.spring.springbootsecurityparkkyounghoon.dto;

import com.example.spring.springbootsecurityparkkyounghoon.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponseDTO {
    private long id;
    private String userId;
    private String userName;
    private Role role;
}