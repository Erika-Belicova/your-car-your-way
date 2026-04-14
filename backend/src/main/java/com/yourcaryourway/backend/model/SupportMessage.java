package com.yourcaryourway.backend.model;

import com.yourcaryourway.backend.enumeration.SenderType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * Entity representing a support message.
 * Contains content, senderType and the associated support conversation.
 * Tracks creation and update timestamps automatically.
 */
@Entity
@Table(name = "support_messages")
@Data
public class SupportMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "sent_at", nullable = false, updatable = false)
    private OffsetDateTime sentAt;

    @Enumerated(EnumType.STRING)
    @JdbcType(org.hibernate.dialect.PostgreSQLEnumJdbcType.class)
    @Column(name = "sender_type", nullable = false)
    private SenderType senderType; // user or support agent

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    // support conversation that this support message belongs to
    private SupportConversation supportConversation;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

}
