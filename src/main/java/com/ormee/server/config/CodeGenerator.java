package com.ormee.server.config;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CodeGenerator {

    private final Random random = new Random();

    public int generateCode() {
        return 1000 + random.nextInt(9000);
    }
}
