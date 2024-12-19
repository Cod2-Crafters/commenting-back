package com.codecrafter.commenting.common.batch;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzJobConfig {
//    쿼츠 스케쥴러 추후구현
//    @Bean
//    public JobDetail jobDetail() {
//        return JobBuilder.newJob(BatchJobLauncher.class)
//            .withIdentity("batchJobLauncher")
//            .storeDurably()
//            .build();
//    }
//

//    @Bean
//    public Trigger trigger(JobDetail jobDetail) {
//        return TriggerBuilder.newTrigger()
//            .forJob(jobDetail)
//            .withIdentity("batchJobTrigger")
//            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
//                .withIntervalInHours(24) // 24시간마다 실행
//                .repeatForever()) // 무한 반복
//            .build();
//    }
}
