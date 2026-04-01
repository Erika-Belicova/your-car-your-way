package com.yourcaryourway.backend.dto.support.conversation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for creating a new support conversation with an initial message.
 */
@Data
public class SupportConversationRequestDTO {

    @NotBlank(message = "Subject cannot be blank")
    @Schema(description = "Subject of the support conversation", example = "Problem with my reservation")
    private String subject;

    @NotBlank(message = "Message content cannot be blank")
    @Schema(description = "Content of the initial message", example = "Hello, I need help to change my reservation")
    private String messageContent;

}