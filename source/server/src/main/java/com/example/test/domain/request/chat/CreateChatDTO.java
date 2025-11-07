package com.example.test.domain.request.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CreateChatDTO {
    @NotBlank(message = "Content must not be blank")
    private String content;
}
