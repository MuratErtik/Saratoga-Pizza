package com.example.saratogapizza.requests;

import lombok.Data;

@Data
public class SetPasswordRequest {

    private String email;

    private String password;

    private String confirmPassword;
}
