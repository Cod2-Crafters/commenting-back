package com.codecrafter.commenting.controller;

import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.entity.Conversation;
import com.codecrafter.commenting.domain.entity.ConversationMST;
import com.codecrafter.commenting.domain.entity.MemberAuth;
import com.codecrafter.commenting.domain.request.conversation.CreateConversationRequest;
import com.codecrafter.commenting.domain.request.conversation.UpdateConversationRequest;
import com.codecrafter.commenting.domain.response.conversation.ConversationDetailsResponse;
import com.codecrafter.commenting.domain.response.conversation.ConversationPageResponse;
import com.codecrafter.commenting.domain.response.conversation.ConversationResponse;
import com.codecrafter.commenting.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 대화 관련 컨트롤러
 */
@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Slf4j
public class ConversationController {
    private final ConversationService conversationService;

    @Operation(summary = "질문조회",
        description = """
                        ★질문 단건 조회</br>
                        {host}/api/conversations/question/{id}
                        """)
    @GetMapping("/question/{id}")
    public ResponseEntity<ApiResponse> getQuestion(@PathVariable Long id) {
        ConversationResponse conversation = conversationService.getConversation(id);
        return new ResponseEntity<>(ApiResponse.success(conversation), HttpStatus.OK);
    }
    @Operation(summary = "대화 상세 조회",
        description = """
                        ★질/답 상세 조회</br>
                        {host}/api/conversations/details/{mstId}
                        """)
    @GetMapping("/details/{mstId}")
    public ResponseEntity<ApiResponse> getConversationDetails(@PathVariable Long mstId,
                                                              @AuthenticationPrincipal MemberAuth memberAuth) {
        List<ConversationDetailsResponse> details = conversationService.getConversationDetails(mstId, memberAuth);
        return new ResponseEntity<>(ApiResponse.success(details), HttpStatus.OK);
    }

    @Operation(summary = "대화 전체 조회(삭제예정/조회용으로 남겨둠)",
        description = """
                        ★해당 스페이스의 모든 대화 조회</br>
                        {host}/api/conversations/timeline/{ownerId}
                        """)
    @GetMapping("/timeline/{ownerId}")
    public ResponseEntity<ApiResponse> getConversationTimeline(@PathVariable Long ownerId,
                                                               @AuthenticationPrincipal MemberAuth memberAuth) {
        List<ConversationDetailsResponse> details = conversationService.getConversationByOwnerId(ownerId, memberAuth);
        return new ResponseEntity<>(ApiResponse.success(details), HttpStatus.OK);
    }

    @Operation(summary = "대화 전체 조회(페이징)",
        description = """
                        ★대화 블럭단위로 스페이스의 대화 조회</br>
                        초기 블럭 3개, 추가 요청시 블럭 3개</br>
                        {host}/api/conversations/timeline/{ownerId}/{page}
                        """)
    @GetMapping("/timeline/{ownerId}/{page}")
    public ResponseEntity<ApiResponse> getConversationTimeline(@PathVariable Long ownerId,
                                                               @PathVariable(required = false) Integer page,
                                                               @AuthenticationPrincipal MemberAuth memberAuth) {
        ConversationPageResponse conversation = conversationService.getConversationPage(ownerId, page, memberAuth);
        return new ResponseEntity<>(ApiResponse.success(conversation), HttpStatus.OK);
    }

    @Operation(summary = "질문작성",
        description = """
                        ★스페이스에 불특정 다수의 질문자가 질문</br>
                        {host}/api/conversations/question</br>
                        입력값 ==================================</br>
                                 "ownerId": 주인장아이디,</br>
                                 "guestId": 질문자아이디,</br>
                                 "content": 내용</br>
                        ==================================
                        """)
    @PostMapping("/question")
    public ResponseEntity<ApiResponse> CreateQuestion(@RequestBody CreateConversationRequest request) {
        List<ConversationResponse> conversation = conversationService.createConversation(request);
        return new ResponseEntity<>(ApiResponse.success(conversation), HttpStatus.CREATED);
    }
    @Operation(summary = "질문수정",
        description = """
                        ★질문자가 질문내용 수정</br>
                        {host}/api/conversations/question/update
                        """)
    @PutMapping("/question/update")
    public ResponseEntity<ApiResponse> updateQuestion(@RequestBody UpdateConversationRequest request) {
        Conversation conversation = conversationService.updateConversation(request);
        return new ResponseEntity<>(ApiResponse.success(conversation.getId()), HttpStatus.OK);
    }

    @Operation(summary = "질문삭제",
        description = """
                        ★질문 삭제시 관련대화(질문1 + 답변n) 일괄 삭제</br>
                        {host}/api/conversations/question/{id}</br>
                        """)
    @DeleteMapping("/question/{id}")
    public ResponseEntity<ApiResponse> deleteQuestion(@PathVariable Long id) {
        conversationService.deleteConversationAndDetails(id);
        return new ResponseEntity<>(ApiResponse.success(id), HttpStatus.OK);
    }

    @Operation(summary = "답변작성",
        description = """
                        ★스페이스 주인이 질문에 대한 답변 작성</br>
                        {host}/api/conversations/answer</br>
                        입력값 ==================================</br>
                                 "mstId": 대화식별자,</br>
                                 "ownerId": 주인장아이디,</br>
                                 "guestId": 질문자아이디,</br>
                                 "content": 내용</br>
                        ==================================
                        """)
    @PostMapping("/answer")
    public ResponseEntity<ApiResponse> creatAnswer(@RequestBody CreateConversationRequest request) {
        Conversation conversation = conversationService.addAnswer(request);
        return new ResponseEntity<>(ApiResponse.success(conversation.getId()), HttpStatus.CREATED);
    }

    @Operation(summary = "답변수정",
        description = """
                        ★답변자가 답변내용 수정</br>
                        {host}/api/conversations/answer/update
                        """)
    @PutMapping("/answer/update")
    public ResponseEntity<ApiResponse> updateAnswer(@RequestBody UpdateConversationRequest request) {
        Conversation conversation = conversationService.updateAddAnswer(request);
        return new ResponseEntity<>(ApiResponse.success(conversation.getId()), HttpStatus.OK);
    }

    @Operation(summary = "답변삭제",
        description = """
                        ★답변 단건 삭제</br>
                        {host}/api/conversations/answer/{id}</br>
                        id = con_id(== sub_id)
                        """)
    @DeleteMapping("/answer/{id}")
    public ResponseEntity<ApiResponse> deleteAnswer(@PathVariable Long id) {
        conversationService.deleteAnswer(id);
        return new ResponseEntity<>(ApiResponse.success(id), HttpStatus.OK);
    }

}
