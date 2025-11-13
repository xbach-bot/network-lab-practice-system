package com.example.test.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.domain.Submission;
import com.example.test.domain.User;
import com.example.test.domain.response.ResponsePaginationDTO;
import com.example.test.service.SubmissionService;
import com.example.test.service.UserService;
import com.turkraft.springfilter.boot.Filter;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {
    private final SubmissionService submissionService;

    private final UserService userService;

    public SubmissionController(SubmissionService submissionService, UserService userService) {
        this.submissionService = submissionService;
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<ResponsePaginationDTO> getSubmissions(@Filter Specification<Submission> spec,
            Pageable pageable) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = this.userService.getUserByEmail(email);
        return new ResponseEntity<>(
                this.submissionService.getSubmissionsByUser(spec, pageable, currentUser.getStudentId()), HttpStatus.OK);

    }

    @GetMapping("/by-qcode/{qCode}")
    public ResponseEntity<ResponsePaginationDTO> getSubmissionsByQCode(@Filter Specification<Submission> spec,
            Pageable pageable, @PathVariable String qCode) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = this.userService.getUserByEmail(email);
        return new ResponseEntity<>(
                this.submissionService.getSubmissionsByUserAndqCode(spec, currentUser.getStudentId(), qCode, pageable),
                HttpStatus.OK);
    }

    @GetMapping("/user/ranking")
    public ResponseEntity<ResponsePaginationDTO> getUsersRanking(Pageable pageable) {
        return new ResponseEntity<>(this.submissionService.getUserRanking(pageable), HttpStatus.OK);
    }

}
