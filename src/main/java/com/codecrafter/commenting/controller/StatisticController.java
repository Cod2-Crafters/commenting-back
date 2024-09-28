package com.codecrafter.commenting.controller;

import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.response.StatisticsResponse;
import com.codecrafter.commenting.service.StatisticService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticController {

    private final StatisticService statisticService;

    @Operation(summary = "통계 ★",
        description = """
                        ★통계</br>
                        토큰 필수</br>
                        {host}/api/statistics
                        """)
    @GetMapping
    public ResponseEntity<ApiResponse> getStatistic() {
        StatisticsResponse statisticsResponse = statisticService.getStatistics();
        return ResponseEntity.ok().body(ApiResponse.success(statisticsResponse));
    }

}
