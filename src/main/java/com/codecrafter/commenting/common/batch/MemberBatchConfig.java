package com.codecrafter.commenting.common.batch;

import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.repository.MemberInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableBatchProcessing
@Slf4j
public class MemberBatchConfig {

    private final MemberInfoRepository memberRepository;

    public MemberBatchConfig(MemberInfoRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Bean
    public Job updateOldMembersJob(JobRepository jobRepository, Step updateOldMembersStep) {
        return new JobBuilder("updateOldMembersJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(updateOldMembersStep)
            .build();
    }

    @Bean
    public Step updateOldMembersStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("updateOldMembersStep", jobRepository)
            .<MemberInfo, MemberInfo>chunk(10, transactionManager)
            .reader(memberInfoItemReader())
            .processor(memberInfoItemProcessor())
            .writer(memberInfoItemWriter())
            .build();
    }

    @Bean
    public ItemReader<MemberInfo> memberInfoItemReader() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(2);
        List<MemberInfo> membersToUpdate = memberRepository.findByCreatedAtBefore(cutoffDate);
        return new ListItemReader<>(membersToUpdate);
    }

    @Bean
    public ItemProcessor<MemberInfo, MemberInfo> memberInfoItemProcessor() {
        return memberInfo -> {
            memberInfo.setNickname("");
            memberInfo.setIntroduce("");
            return memberInfo;
        };
    }

    @Bean
    public ItemWriter<MemberInfo> memberInfoItemWriter() {
        return members -> memberRepository.saveAll(members);
    }
}
