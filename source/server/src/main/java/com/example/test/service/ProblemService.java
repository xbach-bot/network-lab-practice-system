package com.example.test.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.test.core.error.BadRequestException;
import com.example.test.domain.Problem;
import com.example.test.domain.response.ResponseMetaDTO;
import com.example.test.domain.response.ResponsePaginationDTO;
import com.example.test.domain.response.problem.ResponseProblemDTO;
import com.example.test.repository.ProblemRepository;

@Service
public class ProblemService {

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Problem findByQCode(String qCode) {
        return problemRepository.findByqCode(qCode)
                .orElseThrow(() -> new RuntimeException("Problem not found: " + qCode));
    }

    public ResponsePaginationDTO getProblems(Specification<Problem> spec, Pageable pageable, String studentId) {
        Page<Problem> problem = this.problemRepository.findAll(spec, pageable);

        ResponsePaginationDTO resultPaginationDTO = new ResponsePaginationDTO();

        ResponseMetaDTO meta = new ResponseMetaDTO();

        meta.setCurrent(problem.getNumber() + 1);
        meta.setPageSize(problem.getSize());

        meta.setPages(problem.getTotalPages());
        meta.setTotal(problem.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        resultPaginationDTO.setResult(problem.getContent().stream().map(problemEntity -> {
            ResponseProblemDTO problemDTO = new ResponseProblemDTO();

            boolean isSolved = problemEntity.getSubmissions().stream()
                    .anyMatch(submission -> submission.getUser().getStudentId().equals(studentId)
                            && submission.getCorrect());

            modelMapper.map(problemEntity, problemDTO);
            problemDTO.setSolved(isSolved);
            return problemDTO;

        }).collect(Collectors.toList()));
        return resultPaginationDTO;
    }

    public ResponseProblemDTO getProblemByQcode(String qCode, String studentId) throws BadRequestException {
        try {
            Problem problem = this.findByQCode(qCode);
            boolean isSolved = problem.getSubmissions().stream()
                    .anyMatch(
                            submission -> submission.getCorrect()
                                    && submission.getUser().getStudentId().equals(studentId));
            ResponseProblemDTO problemDTO = modelMapper.map(problem, ResponseProblemDTO.class);
            problemDTO.setSolved(isSolved);
            return problemDTO;
        } catch (Exception e) {
            throw new BadRequestException("Problem not found: " + qCode);
        }
    }

    public Problem save(Problem problem) {
        return problemRepository.save(problem);
    }
}
