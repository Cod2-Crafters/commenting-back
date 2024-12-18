package com.codecrafter.commenting.controller;

import com.codecrafter.commenting.common.batch.MemberScheduler;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
@Slf4j
public class BatchController {

    private MemberScheduler memberScheduler;

    @GetMapping("/memberBatch")
    public void memberBatch() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.schedule(() -> {
            memberScheduler.updateMemberIntroductions();
        }, 5, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            memberScheduler.updateMemberIntroductions();
        }, 0, 10, TimeUnit.SECONDS);
    }
}
