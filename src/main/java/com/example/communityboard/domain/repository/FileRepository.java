package com.example.communityboard.domain.repository;

import com.example.communityboard.domain.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}