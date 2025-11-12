package com.example.test.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "problems")
@NoArgsConstructor
@Setter
@Getter
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "q_code")
    private String qCode;
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String description;
    private String protocolType;
    private String type;
    private String ioType;
    private int port;

    private String handlerName;

    private Instant createdAt;

    @OneToMany(mappedBy = "problem")
    @JsonIgnore
    private List<Submission> submissions;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }
}