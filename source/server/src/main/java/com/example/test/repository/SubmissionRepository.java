package com.example.test.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.test.domain.Submission;
import com.example.test.domain.response.user.IUserRank;

public interface SubmissionRepository extends JpaRepository<Submission, Long>, JpaSpecificationExecutor<Submission> {

    Submission findById(long id);

    @Query("""
                SELECT
                    u.id AS userId,
                    u.name AS name,
                    u.email AS email,
                    u.studentId AS studentId,
                    u.role AS role,
                    COUNT(s.id) AS totalSubmissions,
                    COUNT(DISTINCT CASE WHEN s.correct = true THEN s.problem.id END) AS correctSubmissions
                FROM User u
                LEFT JOIN Submission s ON s.user.id = u.id
                GROUP BY u.id, u.name, u.email, u.studentId, u.role
                ORDER BY correctSubmissions DESC, totalSubmissions ASC
            """)
    List<IUserRank> getRanking();

}
