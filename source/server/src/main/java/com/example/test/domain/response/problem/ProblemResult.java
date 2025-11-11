package com.example.test.domain.response.problem;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProblemResult implements Serializable {
    private String inputData; // Dữ liệu server gửi cho client
    private String studentResult; // Client trả về
    private String expectedResult; // Đáp án chuẩn
    private Boolean correct;
    private String status;
}
