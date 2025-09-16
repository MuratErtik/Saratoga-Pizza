package com.example.saratogapizza.requests;

import lombok.Data;

@Data
public class ChangePasswordRequest {

    private String oldPassword;

    private String newPassword;

    private String confirmPassword;

    private String mail;

}
