package com.example.test.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.test.core.error.BadRequestException;
import com.example.test.domain.Problem;
import com.example.test.domain.SubmissionFile;
import com.example.test.domain.User;
import com.example.test.domain.response.ResponsePaginationDTO;
import com.example.test.domain.response.ResponseString;
import com.example.test.domain.response.problem.ResponseProblemDTO;
import com.example.test.domain.response.submissionfile.ResponseSubmissionFileDTO;
import com.example.test.repository.ProblemRepository;
import com.example.test.repository.UserRepository;
import com.example.test.service.SubmissionFileService;
import com.turkraft.springfilter.boot.Filter;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/submit-file")
public class SubmissionFileController {
    private final SubmissionFileService submissionFileService;
    private final ProblemRepository problemRepo;
    private final UserRepository userRepo;
    private final ModelMapper modelMapper;

    public SubmissionFileController(SubmissionFileService submissionFileService, ProblemRepository problemRepo, UserRepository userRepo, ModelMapper modelMapper) {
        this.submissionFileService = submissionFileService;
        this.problemRepo = problemRepo;
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
    }

    @Value("${app.submissions.dir}")
    private String submissionsDir;

    @PostMapping("/problems/{qcode}/upload")
    public ResponseEntity<?> upload(@PathVariable String qcode,
                                    @RequestParam("file") MultipartFile file,
                                    @RequestParam(value = "note", required = false) String note
                                    ) throws BadRequestException, IOException {
        
        String original = file.getOriginalFilename();
        if (original == null || !original.toLowerCase().endsWith(".java")) {
            throw new BadRequestException("Only .java files allowed");
        }

        Problem problem = problemRepo.findByqCode(qcode).orElse(null);
        if (problem == null) throw new BadRequestException("Problem not found");
        
        Path outDir = Paths.get(submissionsDir, String.valueOf(problem.getId()));
        Files.createDirectories(outDir);

        String fileName = System.currentTimeMillis() + "_" + original.replaceAll("[^a-zA-Z0-9._-]", "_");
        Path out = outDir.resolve(fileName);

        
        try {
            Files.copy(file.getInputStream(), out, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            throw new BadRequestException("Failed to save file");
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = this.userRepo.findByEmail(email);
        SubmissionFile sf = new SubmissionFile();
        sf.setFilePath(out.toString());
        sf.setCreatedAt(Instant.now());
        sf.setProblem(problem);
        sf.setUser(currentUser);


        System.out.println(out.toString());
        SubmissionFile saveFile = submissionFileService.save(sf);
        ResponseSubmissionFileDTO res = new ResponseSubmissionFileDTO();
        res.setFilePath(out.toString());
        res.setProblem(modelMapper.map(problem, ResponseProblemDTO.class));
        res.setCreatedAt(Instant.now());
        res.setId(saveFile.getId());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/me")
    public ResponseEntity<ResponsePaginationDTO> getFileByUser(@Filter Specification<SubmissionFile> spec,
            Pageable pageable) {

                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User currentUser = this.userRepo.findByEmail(email);


        return ResponseEntity.ok(
                this.submissionFileService.getSubmissionFilesByUser(spec, pageable, currentUser.getStudentId()));
    }

    @GetMapping("/submissions/{id}/content")
    public ResponseEntity<ResponseString> content(@PathVariable Long id) throws BadRequestException, IOException {
        SubmissionFile sf = submissionFileService.findById(id).orElse(null);
        if (sf == null) throw new BadRequestException("File not found");
        Path p = Paths.get(sf.getFilePath());
        if (!Files.exists(p)) throw new BadRequestException("File not found");
        String content = Files.readString(p);
        return ResponseEntity.ok().body(new ResponseString(content));
    }
    
}
