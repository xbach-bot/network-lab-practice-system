package com.example.test.domain.response.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResponseUserRankDTO {
    private ResponseUserDTO user;
    private int totalSubmissions;
    private int correctSubmissions;

}
