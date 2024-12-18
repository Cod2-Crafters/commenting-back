package com.codecrafter.commenting.common.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import com.codecrafter.commenting.domain.entity.MemberInfo;
import com.codecrafter.commenting.repository.MemberInfoRepository;

@Component
@RequiredArgsConstructor
public class MemberScheduler {
    private MemberInfoRepository memberInfoRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateMemberIntroductions() {

        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(2);
        List<MemberInfo> oldMembers = memberInfoRepository.findByCreatedAtBefore(cutoffDate);

        for (MemberInfo member : oldMembers) {
            member.setIntroduce(null);
            memberInfoRepository.save(member);
        }

        System.out.println("Old members' introductions have been updated to null.");
    }
}
