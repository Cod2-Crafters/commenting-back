package com.codecrafter.commenting.common.batch;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.JobBuilder;
import org.quartz.TriggerBuilder;
import org.quartz.CronScheduleBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class BatchConfig {

    @Bean
    public JobDetail batchJobLauncherDetail() {
        return JobBuilder.newJob(BatchJobLauncher.class)
            .withIdentity("batchJobLauncher", "batchGroup")
            .storeDurably()
            .build();
    }

    @Bean
    public Trigger batchJobLauncherTrigger() {
        return TriggerBuilder.newTrigger()
            .forJob(batchJobLauncherDetail())
            .withIdentity("batchJobLauncherTrigger", "batchGroup")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 12 * * ?"))
            .build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactory(Trigger batchJobLauncherTrigger, JobDetail batchJobLauncherDetail) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobDetails(batchJobLauncherDetail);
        factory.setTriggers(batchJobLauncherTrigger);
        return factory;
    }
}
