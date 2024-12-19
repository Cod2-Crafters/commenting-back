package com.codecrafter.commenting.common.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private JobLauncher jobLauncher;
    private Job job;

    @Scheduled(cron = "0 0 12 * * ?")
    public void runBatchJob() {
        try {
            JobExecution execution = jobLauncher.run(job, new JobParameters());
            System.out.println("Batch Job 실행 완료: " + execution.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
