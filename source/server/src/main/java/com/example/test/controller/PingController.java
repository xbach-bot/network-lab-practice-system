package com.example.test.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.test.domain.response.ResponseString;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/ping")
public class PingController {
    
    @GetMapping("")
    public ResponseEntity<ResponseString> ping() {
        return ResponseEntity.ok(new ResponseString("pong"));
    }
}
