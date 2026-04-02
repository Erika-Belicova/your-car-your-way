package com.yourcaryourway.backend.repository;

import com.yourcaryourway.backend.enumeration.ConversationStatus;
import com.yourcaryourway.backend.model.SupportConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for SupportConversation entity with methods
 * to find support conversations by user ID, status, and chat session ID.
 */
@Repository
public interface SupportConversationRepository extends JpaRepository<SupportConversation, Long> {
    List<SupportConversation> findByUserId(Long userId);
    List<SupportConversation> findByStatus(ConversationStatus status);
    Optional<SupportConversation> findByChatSessionId(UUID chatSessionId);
}
