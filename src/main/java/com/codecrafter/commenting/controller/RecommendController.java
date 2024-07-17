package com.codecrafter.commenting.controller;


import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.request.RecommendRequest;
import com.codecrafter.commenting.domain.response.RecommendResponse;
import com.codecrafter.commenting.service.RecommendService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
@Slf4j
public class RecommendController {
    private final RecommendService recommendService;

    @Operation(summary = "좋아요",
        description = """
                        ★좋아요 증/감</br>
                        {host}/api/recommend/likes
                        
                        """)
    @PostMapping("/like")
    public ResponseEntity<ApiResponse> incrementLikes(@RequestBody RecommendRequest request) {
        RecommendResponse response = recommendService.incrementLikes(request);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

}
