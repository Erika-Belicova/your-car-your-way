package com.yourcaryourway.backend.model;

import com.yourcaryourway.backend.enumeration.ConversationStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a support conversation.
 * Contains chatSessionId, subject, status, associated user,
 * as well as linked support messages.
 * Tracks creation and update timestamps automatically.
 */
@Entity
@Table(name = "support_conversations")
@Data
public class SupportConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "chat_session_id")
    private UUID chatSessionId;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @JdbcType(org.hibernate.dialect.PostgreSQLEnumJdbcType.class)
    private ConversationStatus status = ConversationStatus.OPEN;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // user initiating the support conversation

    @OneToMany(mappedBy = "supportConversation", cascade = CascadeType.ALL, orphanRemoval = true)
    // support messages belonging to this support conversation
    private List<SupportMessage> supportMessages = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

}
