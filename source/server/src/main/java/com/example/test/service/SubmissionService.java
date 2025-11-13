package com.example.test.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.test.domain.Problem;
import com.example.test.domain.Submission;
import com.example.test.domain.User;
import com.example.test.domain.response.ResponseMetaDTO;
import com.example.test.domain.response.ResponsePaginationDTO;
import com.example.test.domain.response.problem.ProblemResult;
import com.example.test.domain.response.problem.ResponseProblemDTO;
import com.example.test.domain.response.submission.ResponseSubmissionDTO;
import com.example.test.domain.response.user.IUserRank;
import com.example.test.domain.response.user.ResponseUserDTO;
import com.example.test.domain.response.user.ResponseUserRankDTO;
import com.example.test.repository.SubmissionRepository;
import com.example.test.socket.websocket.AppGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AppGateway appGateway;

    
    public Submission save(String studentId, String qCode, ProblemResult result) {

        try {
            User user = userService.findByStudentId(studentId);
            Problem problem = problemService.findByQCode(qCode);
            Submission submission = new Submission();
            submission.setUser(user);
            submission.setStudentResult(result.getStudentResult());
            submission.setExpectedResult(result.getExpectedResult());
            submission.setCorrect(result.getCorrect());
            submission.setCreatedAt(Instant.now());
            submission.setProblem(problem);
            submission.setInputData(result.getInputData());
            submission.setStatus(result.getStatus());

            Submission res = submissionRepository.save(submission);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(res);
            appGateway.sendEventToClient(user.getEmail(), "submission_created", json);

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResponsePaginationDTO getSubmissionsByUser(Specification<Submission> spec, Pageable pageable,
            String studentId) {
        
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Submission> submissions = this.submissionRepository.findAll(spec.and(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("studentId"), studentId)),
                sortedPageable);

        ResponsePaginationDTO resultPaginationDTO = new ResponsePaginationDTO();

        ResponseMetaDTO meta = new ResponseMetaDTO();

        meta.setCurrent(submissions.getNumber() + 1);
        meta.setPageSize(submissions.getSize());

        meta.setPages(submissions.getTotalPages());
        meta.setTotal(submissions.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        resultPaginationDTO.setResult(submissions.getContent().stream().map(sub -> {
            ResponseUserDTO u = modelMapper.map(sub.getUser(), ResponseUserDTO.class);

            ResponseProblemDTO p = modelMapper.map(sub.getProblem(), ResponseProblemDTO.class);

            p.setSolved(sub.getProblem().getSubmissions().stream()
                    .anyMatch(
                            submission -> submission.getCorrect()));

            ResponseSubmissionDTO submissionDTO = modelMapper.map(sub, ResponseSubmissionDTO.class);
            submissionDTO.setUser(u);
            submissionDTO.setProblem(p);
            submissionDTO.setCreatedAt(sub.getCreatedAt());

            return submissionDTO;
        }).collect(Collectors.toList()));
        return resultPaginationDTO;
    }

    public ResponsePaginationDTO getSubmissionsByUserAndqCode(Specification<Submission> spec, String studentId,
            String qCode, Pageable pageable) {
        Page<Submission> submissions = this.submissionRepository
                .findAll(spec.and((root, query, criteriaBuilder) -> criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("user").get("studentId"), studentId),
                        criteriaBuilder.equal(root.get("problem").get("qCode"), qCode))), pageable);

        ResponsePaginationDTO resultPaginationDTO = new ResponsePaginationDTO();

        ResponseMetaDTO meta = new ResponseMetaDTO();

        meta.setCurrent(submissions.getNumber() + 1);
        meta.setPageSize(submissions.getSize());

        meta.setPages(submissions.getTotalPages());
        meta.setTotal(submissions.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        resultPaginationDTO.setResult(submissions.getContent().stream().map(sub -> {
            ResponseUserDTO u = modelMapper.map(sub.getUser(), ResponseUserDTO.class);

            ResponseProblemDTO p = modelMapper.map(sub.getProblem(), ResponseProblemDTO.class);

            ResponseSubmissionDTO submissionDTO = modelMapper.map(sub, ResponseSubmissionDTO.class);
            submissionDTO.setUser(u);
            submissionDTO.setProblem(p);
            return submissionDTO;
        }).collect(Collectors.toList()));
        return resultPaginationDTO;
    }

    public ResponsePaginationDTO getUserRanking(Pageable pageable) {
        ResponsePaginationDTO ranking = new ResponsePaginationDTO();

        ResponseMetaDTO meta = new ResponseMetaDTO();

        List<IUserRank> allRanking = this.submissionRepository.getRanking();

        List<ResponseUserRankDTO> res = allRanking.stream().map(iUserRank -> {
            ResponseUserRankDTO dto = new ResponseUserRankDTO();
            ResponseUserDTO userDto = new ResponseUserDTO();

            userDto.setId(iUserRank.getUserId());
            userDto.setName(iUserRank.getName());
            userDto.setEmail(iUserRank.getEmail());
            userDto.setStudentId(iUserRank.getStudentId());
            userDto.setRole(iUserRank.getRole());

            dto.setUser(userDto);
            dto.setTotalSubmissions(iUserRank.getTotalSubmissions().intValue());
            dto.setCorrectSubmissions(iUserRank.getCorrectSubmissions().intValue());

            return dto;
        }).toList();

        int totalItems = res.size();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), totalItems);

        meta.setCurrent(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages((int) Math.ceil((double) totalItems / pageable.getPageSize()));
        meta.setTotal(totalItems);
        ranking.setMeta(meta);
        ranking.setResult(res.subList(start, end));

        return ranking;
    }

}
