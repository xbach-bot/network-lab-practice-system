package com.example.test.domain.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResponseString {
    private String message;
    public ResponseString(String message) {
        this.message = message;
    }

}
