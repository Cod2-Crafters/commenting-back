package com.codecrafter.commenting.config.batch;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(BatchJobLauncher.class)
            .withIdentity("batchJobLauncher")
            .storeDurably()
            .build();
    }

    @Bean
    public Trigger trigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity("batchJobTrigger")
            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInHours(24)
                .repeatForever())
            .build();
    }
}
