package com.example.test;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SampleTableMongoRepository
        extends MongoRepository<SampleTableDocument, Long> {
    Optional<List<SampleTableDocument>> findByCol1(Long col1);
    Optional<List<SampleTableDocument>> findByCol20(Long col20);

    Optional<SampleTableDocument> findByIdEquals(Long id);
}