package com.codecrafter.commenting.domain.response.conversation;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConversationPageResponse {
    private List<ConversationDetailsResponse> conversations;
    private boolean lastPage;
}
