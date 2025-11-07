package com.example.test.domain.response.chat;

import java.time.Instant;

import com.example.test.domain.response.user.ResponseUserDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResponseChatDTO {
    private Long id;
    private String content;
    private Instant createdAt, updatedAt;
    private ResponseUserDTO user;

}
