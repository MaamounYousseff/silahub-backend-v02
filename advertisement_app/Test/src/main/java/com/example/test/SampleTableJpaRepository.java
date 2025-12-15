package com.example.test;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SampleTableJpaRepository
        extends JpaRepository<SampleTableEntity, Long> {


    Optional<List<SampleTableEntity>> findByCol1(Long col1);
    Optional<List<SampleTableEntity>> findByCol20(Long col20);
}
