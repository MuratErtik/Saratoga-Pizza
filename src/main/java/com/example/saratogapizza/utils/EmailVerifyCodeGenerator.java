package com.example.saratogapizza.utils;

import java.util.Random;

public class EmailVerifyCodeGenerator {

    public static String generateCode() {

        int codeLength = 6;

        Random random = new Random();

        StringBuilder code = new StringBuilder(codeLength);

        for (int i = 0; i < codeLength; i++) {
            code.append(random.nextInt(10));

        }
        return code.toString();
    }
}