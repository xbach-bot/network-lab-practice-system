package com.example.test.domain.response.user;

public interface IUserRank {
    Long getUserId();

    String getName();

    String getEmail();

    String getStudentId();

    String getRole();

    Long getTotalSubmissions();

    Long getCorrectSubmissions();
}
