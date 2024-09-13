package com.codecrafter.commenting.controller;


import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.request.RecommendRequest;
import com.codecrafter.commenting.domain.response.RecommendResponse;
import com.codecrafter.commenting.domain.response.conversation.ConversationResponse;
import com.codecrafter.commenting.service.RecommendService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    @PostMapping("/likes")
    public ResponseEntity<ApiResponse> updateLikes(@RequestBody RecommendRequest request) {
        RecommendResponse response = recommendService.updateLikes(request);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @Operation(summary = "고마워요",
        description = """
                        ★고마워요 증/감</br>
                        {host}/api/recommend/thanked
                        
                        """)
    @PostMapping("/thanked")
    public ResponseEntity<ApiResponse> updateThanked(@RequestBody RecommendRequest request) {
        RecommendResponse response = recommendService.updateThanked(request);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @Operation(summary = "좋은질문들 ★",
        description = """
                        ★내가 좋아요누른 대화 조회</br>
                        {host}/api/recommends/user
                        """)
    @PostMapping("/user")
    public ResponseEntity<ApiResponse> getRecommendedConversations(@RequestHeader("Authorization") String token) {
        List<ConversationResponse> conversations = recommendService.getRecommendedConversations(token);
        return new ResponseEntity<>(ApiResponse.success(conversations), HttpStatus.OK);
    }

}
