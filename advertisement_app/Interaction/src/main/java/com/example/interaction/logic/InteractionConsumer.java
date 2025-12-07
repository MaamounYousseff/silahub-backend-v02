package com.example.interaction.logic;

import com.example.interaction.domain.repo.PostInteractionsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InteractionConsumer
{
    @Autowired
    private PostInteractionsRepo postInteractionsRepo;



}
