package com.yourcaryourway.backend.dto.support.message;

import com.yourcaryourway.backend.enumeration.SenderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * DTO representing a support message.
 */
@Data
public class SupportMessageResponseDTO {

    @Schema(description = "Unique identifier of the message", example = "1")
    private Long id;

    @Schema(description = "Content of the message", example = "Hello, how can I change my reservation")
    private String content;

    @Schema(description = "Type of sender", example = "USER")
    private SenderType senderType;

    @Schema(description = "Date and time the message was sent")
    private OffsetDateTime sentAt;

}