package com.example.spring.springbootsecurityparkkyounghoon.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogoutResponseDTO {
    String message;
    String url;
}