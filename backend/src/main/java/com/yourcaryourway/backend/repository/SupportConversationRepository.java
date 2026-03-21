package com.yourcaryourway.backend.repository;

import com.yourcaryourway.backend.enumeration.ConversationStatus;
import com.yourcaryourway.backend.model.SupportConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for SupportConversation entity with methods
 * to find support conversations by user ID, chat session ID, and status.
 */
@Repository
public interface SupportConversationRepository extends JpaRepository<SupportConversation, Long> {
    List<SupportConversation> findByUserId(Long userId);
    List<SupportConversation> findByChatSessionId(UUID chatSessionId);
    List<SupportConversation> findByStatus(ConversationStatus status);
}
