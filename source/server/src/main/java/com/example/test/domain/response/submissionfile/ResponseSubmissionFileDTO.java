package com.example.test.domain.response.submissionfile;

import java.time.Instant;

import com.example.test.domain.response.problem.ResponseProblemDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class ResponseSubmissionFileDTO {
    private Long id;

    private String filePath;
    private Instant createdAt;

    private ResponseProblemDTO problem;
}
