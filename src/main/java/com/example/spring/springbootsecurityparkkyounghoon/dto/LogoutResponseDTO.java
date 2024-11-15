package com.example.spring.springbootsecurityparkkyounghoon.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class LogoutResponseDTO {
    String message;
    String url;
}