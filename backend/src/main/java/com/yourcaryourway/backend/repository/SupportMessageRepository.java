package com.yourcaryourway.backend.repository;

import com.yourcaryourway.backend.model.SupportMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for SupportMessage entity with a method to find support messages
 * by the support conversation ID.
 */
@Repository
public interface SupportMessageRepository extends JpaRepository<SupportMessage, Long> {
    // fetch all messages associated with a specific support conversation
    List<SupportMessage> findBySupportConversationId(Long conversationId);
}
