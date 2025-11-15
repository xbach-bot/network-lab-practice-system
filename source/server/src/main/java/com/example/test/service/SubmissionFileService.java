package com.example.test.service;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.test.domain.SubmissionFile;
import com.example.test.domain.response.ResponseMetaDTO;
import com.example.test.domain.response.ResponsePaginationDTO;
import com.example.test.domain.response.problem.ResponseProblemDTO;
import com.example.test.domain.response.submissionfile.ResponseSubmissionFileDTO;
import com.example.test.repository.SubmissionFileRepository;

@Service
public class SubmissionFileService {
    
    private final SubmissionFileRepository submissionFileRepository;

    private final ModelMapper modelMapper;

    public SubmissionFileService(SubmissionFileRepository submissionFileRepository, ModelMapper modelMapper) {
        this.submissionFileRepository = submissionFileRepository;
        this.modelMapper = modelMapper;
    }

    public SubmissionFile save(SubmissionFile submissionFile) {
        return this.submissionFileRepository.save(submissionFile);
    }

    public java.util.Optional<SubmissionFile> findById(Long id) {
        return this.submissionFileRepository.findById(id);
    }

    public ResponsePaginationDTO getSubmissionFilesByUser(Specification<SubmissionFile> spec, Pageable pageable, String studentId) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<SubmissionFile> submissions = this.submissionFileRepository.findAll(spec.and(
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
            ResponseProblemDTO p = modelMapper.map(sub.getProblem(), ResponseProblemDTO.class);

            p.setSolved(sub.getProblem().getSubmissions().stream()
                    .anyMatch(
                            submission -> submission.getCorrect() && submission.getUser().getStudentId().equals(studentId)));

            ResponseSubmissionFileDTO submissionDTO = modelMapper.map(sub, ResponseSubmissionFileDTO.class);
            submissionDTO.setProblem(p);
            submissionDTO.setCreatedAt(sub.getCreatedAt());

            return submissionDTO;
        }).collect(Collectors.toList()));
        return resultPaginationDTO;
    }
}
