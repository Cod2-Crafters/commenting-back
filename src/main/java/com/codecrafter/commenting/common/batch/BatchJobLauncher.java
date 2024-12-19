package com.codecrafter.commenting.common.batch;

import com.codecrafter.commenting.domain.entity.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.codecrafter.commenting.repository.MemberInfoRepository;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BatchJobLauncher extends QuartzJobBean {

    private MemberInfoRepository memberInfoRepository;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(2);
        List<MemberInfo> oldMembers = memberInfoRepository.findByCreatedAtBefore(cutoffDate);

        for (MemberInfo member : oldMembers) {
            member.setNickname("");
            member.setIntroduce("");
            memberInfoRepository.save(member);
        }
        System.out.println("Old member profiles updated successfully.");
    }
}
