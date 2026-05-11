package com.skillbridge.repository;

import com.skillbridge.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    /**
     * Find all conversations (for listing active conversations)
     * In a real app, you'd want to join with user_conversations table
     * to filter by current user
     */
    @Query("SELECT c FROM Conversation c ORDER BY c.createdAt DESC")
    List<Conversation> findAllConversations();
}
