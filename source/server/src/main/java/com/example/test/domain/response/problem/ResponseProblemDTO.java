package com.example.test.domain.response.problem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResponseProblemDTO {
    private Long id;
    private String title, description, qCode, protocolType, type, ioType;
    private boolean solved;
}
