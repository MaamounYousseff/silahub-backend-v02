package com.example.test;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "test_array")
@Getter
@Setter
public class TestArray {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    // PostgreSQL TEXT[] array
    @Type(ListArrayType.class)
    @Column(name = "names", columnDefinition = "TEXT[]")
    private List<String> names;
}

