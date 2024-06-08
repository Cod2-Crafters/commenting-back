package com.codecrafter.commenting.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.entity.MemberAuth;
import com.codecrafter.commenting.domain.request.ConversationRequest;
import com.codecrafter.commenting.domain.response.ConversationMSTResponse;
import com.codecrafter.commenting.service.ConversationService;

import lombok.RequiredArgsConstructor;

/**
 * 대화 관련 컨트롤러
 */
@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

	private final ConversationService conversationService;

	@GetMapping("/{ownerId}")
	public ApiResponse getConversationList(@AuthenticationPrincipal MemberAuth memberAuth,
		@PathVariable("ownerId") Long ownerId) {
		List<ConversationMSTResponse> allConversationMST = conversationService.getAllConversationMST(ownerId);
		return ApiResponse.success(allConversationMST);
	}

	@PostMapping("/{ownerId}")
	public ApiResponse createConversation(@AuthenticationPrincipal MemberAuth memberAuth,
		ConversationRequest conversationRequest, @PathVariable("ownerId") Long ownerId) {

		conversationService.createConversation(conversationRequest, memberAuth.getId(), ownerId);

		return ApiResponse.success("test");
	}

}
