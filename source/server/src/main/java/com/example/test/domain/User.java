package com.example.test.domain;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.example.test.domain.response.user.ResponseUserDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String email;
    private String password;
    private String studentId;
    private Instant createdAt;
    private String refreshToken;
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Chat> chats;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();

    }

    public ResponseUserDTO convertResponseUserDto() {
        ResponseUserDTO userDTO = new ResponseUserDTO();
        userDTO.setId(this.getId());
        userDTO.setName(this.getName());
        userDTO.setEmail(this.getEmail());
        userDTO.setStudentId(this.getStudentId());
        userDTO.setRole(this.getRole());

        return userDTO;
    }
}
