package com.example.test;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public class TestArrayRepo
{
    @Autowired
    private EntityManager entityManager;

    public TestArray find (){
        return this.entityManager.find(TestArray.class,UUID.fromString("814e5630-c39f-4e14-b810-555b50afb175"));
    }


}
