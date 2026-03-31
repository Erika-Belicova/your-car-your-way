package com.yourcaryourway.backend.dto.support.conversation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for creating a new support conversation.
 */
@Data
public class SupportConversationRequestDTO {

    @NotNull(message = "Chat session flag is required")
    @Schema(description = "Define whether this is a live chat session or not", example = "true")
    private Boolean isChatSession;

    @NotBlank(message = "Subject cannot be blank")
    @Schema(description = "Subject of the support conversation", example = "Problem with my reservation")
    private String subject;

}