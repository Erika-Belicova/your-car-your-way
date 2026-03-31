package com.yourcaryourway.backend.dto.support.conversation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yourcaryourway.backend.enumeration.ConversationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * DTO representing a support conversation summary for list view.
 */
@Data
public class SupportConversationResponseDTO {

    @Schema(description = "Unique identifier of the support conversation", example = "1")
    private Long id;

    @Schema(description = "Subject of the support conversation", example = "Problem with my reservation")
    private String subject;

    @Schema(description = "Current status of the support conversation")
    private ConversationStatus status;

    @JsonProperty("created_at")
    @Schema(description = "Date and time the conversation was created")
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "Date and time the conversation was last updated")
    private OffsetDateTime updatedAt;

}