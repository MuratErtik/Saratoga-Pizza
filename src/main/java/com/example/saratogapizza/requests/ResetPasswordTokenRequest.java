package com.example.saratogapizza.requests;

import lombok.Data;

@Data
public class ResetPasswordTokenRequest {

    private Long userId;

    private String token;

    private String newPassword;

}
