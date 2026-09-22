package com.example.jellycat.repository;

import com.example.jellycat.entity.ConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationMemberRepository
        extends JpaRepository<ConversationMember, Long> {

    List<ConversationMember> findByConversationId(Long conversationId);

    List<ConversationMember> findByUserId(Long userId);
}
