package com.example.test.repository;

import com.example.test.domain.SubmissionFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SubmissionFileRepository
        extends JpaRepository<SubmissionFile, Long>, JpaSpecificationExecutor<SubmissionFile> {

}