package com.example.test.domain.response.user;

import java.util.List;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResponseUserDTO {
    private Long id;
    private String name;
    private String email;
    private String studentId;
    private String role;

}
