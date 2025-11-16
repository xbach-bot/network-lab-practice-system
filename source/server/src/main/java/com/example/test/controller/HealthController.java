package com.example.test.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.domain.response.ResponseString;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/health")
public class HealthController {
    
    @GetMapping("")
    public ResponseEntity<ResponseString> healthCheck() {
        return ResponseEntity.ok(new ResponseString("OK"));
    }
}
