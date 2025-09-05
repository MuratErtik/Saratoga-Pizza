package com.example.saratogapizza.requests;

import com.example.saratogapizza.entities.Address;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class SignupRequest {

    private String email;

    private String password;

    private String name;

    private String lastname;


}
