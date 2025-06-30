package com.ormee.server.home.controller;

import com.ormee.server.global.response.ResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseDto healthCheck() {
        return ResponseDto.success();
    }
}
