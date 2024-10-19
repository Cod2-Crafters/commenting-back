package com.codecrafter.commenting.config.batch;

import lombok.RequiredArgsConstructor;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BatchTriggerService {

    private Scheduler scheduler;

    public void triggerJob() throws SchedulerException {
        JobKey jobKey = JobKey.jobKey("batchJobLauncher", "batchGroup");
        scheduler.triggerJob(jobKey);
    }
}
