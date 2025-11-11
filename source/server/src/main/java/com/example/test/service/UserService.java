package com.example.test.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.test.core.error.BadRequestException;
import com.example.test.domain.User;
import com.example.test.domain.request.user.RegisterUserDTO;
import com.example.test.domain.response.user.ResponseUserDTO;
import com.example.test.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;

    }

    public RegisterUserDTO register(RegisterUserDTO entity) throws BadRequestException {
        if (this.userRepository.findByEmail(entity.getEmail()) != null) {
            throw new BadRequestException("Email already exists");
        }

        if (this.userRepository.findByStudentId(entity.getStudentId()) != null) {
            throw new BadRequestException("Student ID already exists");
        }

        User user = new User();
        user.setEmail(entity.getEmail());
        user.setName(entity.getName());
        user.setStudentId(entity.getStudentId());
        user.setPassword(bCryptPasswordEncoder.encode(entity.getPassword()));
        user.setRole("STUDENT");

        this.userRepository.save(user);

        return entity;
    }

    public ResponseUserDTO getUserById(long id) throws BadRequestException {
        User user = this.userRepository.findById(id);
        if (user == null) {
            throw new BadRequestException("User not found");
        }
        return user.convertResponseUserDto();
    }

    public User getUserByEmail(String email) {

        return this.userRepository.findByEmail(email);
    }

    public void updateUserToken(String email, String refreshToken) {
        User currUser = this.getUserByEmail(email);
        if (currUser != null) {
            currUser.setRefreshToken(refreshToken);
            this.userRepository.save(currUser);
        }
    }

    public User findByStudentId(String studentId) {
        return this.userRepository.findByStudentId(studentId);
    }

}
