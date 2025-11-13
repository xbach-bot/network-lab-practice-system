package com.example.test.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.test.core.error.BadRequestException;
import com.example.test.domain.Problem;
import com.example.test.domain.User;
import com.example.test.domain.response.ResponsePaginationDTO;
import com.example.test.domain.response.problem.ResponseProblemDTO;
import com.example.test.service.ProblemService;
import com.example.test.service.UserService;
import com.turkraft.springfilter.boot.Filter;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/problems")
public class ProblemController {

    private final ProblemService problemService;

    private final UserService userService;

    public ProblemController(ProblemService problemService, UserService userService) {
        this.problemService = problemService;
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<ResponsePaginationDTO> getProblemsByUser(@Filter Specification<Problem> spec,
            Pageable pageable) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = this.userService.getUserByEmail(email);
        return new ResponseEntity<>(
                this.problemService.getProblems(spec, pageable, currentUser.getStudentId()), HttpStatus.OK);
    }

    @GetMapping("/get-one/{qCode}")
    public ResponseEntity<ResponseProblemDTO> getProblemByQcode(@PathVariable String qCode) throws BadRequestException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = this.userService.getUserByEmail(email);

        ResponseProblemDTO problemDTO = this.problemService.getProblemByQcode(qCode, currentUser.getStudentId());
        return new ResponseEntity<>(problemDTO, HttpStatus.OK);
    }

}
