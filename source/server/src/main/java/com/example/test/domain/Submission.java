package com.example.test.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "submissions")
@NoArgsConstructor
@Setter
@Getter
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "LONGTEXT")
    private String inputData;
    @Column(columnDefinition = "LONGTEXT")
    private String studentResult;
    @Column(columnDefinition = "LONGTEXT")
    private String expectedResult; 
    private boolean correct;
    private Instant createdAt;

    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }

    public boolean getCorrect() {
        return this.correct;
    }
}