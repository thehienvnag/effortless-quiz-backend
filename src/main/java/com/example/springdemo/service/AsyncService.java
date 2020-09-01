package com.example.springdemo.service;

import com.example.springdemo.model.quizes.QuizzesService;
import com.example.springdemo.model.stagingquizzes.StagingQuizzes;
import com.example.springdemo.model.stagingquizzes.StagingQuizzesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncService {

    @Autowired
    private StagingQuizzesService stagingQuizzesService;


    @Async("threadPoolExecutor")
    public CompletableFuture<String> updateLaunchQuizStatus(Long stagingQuizId){
        StagingQuizzes stagingQuizzes = stagingQuizzesService.findOne(stagingQuizId).orElse(null);
        if(stagingQuizzes != null){
            Long waitTime = stagingQuizzes.getEndTime().getTime() - System.currentTimeMillis();
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stagingQuizzes.setStatusId(5);
            stagingQuizzesService.save(stagingQuizzes);
            return CompletableFuture.completedFuture("Successfully end quiz!");
        }
        return CompletableFuture.completedFuture("Failed to end quiz!!");
    }

}
