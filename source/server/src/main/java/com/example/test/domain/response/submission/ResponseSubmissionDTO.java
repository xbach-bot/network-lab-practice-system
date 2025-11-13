package com.example.test.domain.response.submission;

import java.time.Instant;

import com.example.test.domain.response.problem.ResponseProblemDTO;
import com.example.test.domain.response.user.ResponseUserDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResponseSubmissionDTO {
    private Long id;

    private String inputData;
    private String studentResult;
    private String expectedResult;

    private boolean correct;
    private Instant createdAt;

    private String status;

    private ResponseUserDTO user;

    private ResponseProblemDTO problem;
}