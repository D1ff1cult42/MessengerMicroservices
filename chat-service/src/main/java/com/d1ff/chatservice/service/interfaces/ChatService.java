package com.d1ff.chatservice.service.interfaces;

import com.d1ff.chatservice.dto.request.CreateChatRequest;
import com.d1ff.chatservice.dto.request.CreateOneToOneChatRequest;
import com.d1ff.chatservice.dto.request.UpdateChatRequest;
import com.d1ff.chatservice.dto.response.ChatResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface ChatService {
    @Transactional
    ChatResponse createGroupChat(UUID userId, CreateChatRequest createChatRequest, Pageable pageable);

    @Transactional
    ChatResponse createOneToOneChat(UUID userId, CreateOneToOneChatRequest createOneToOneChatRequest, Pageable pageable);

    @Transactional(readOnly = true)
    ChatResponse getChat(UUID chatId, Pageable pageable);

    @Transactional
    void deleteChat(UUID userId, UUID chatId);

    @Transactional
    ChatResponse updateChat(UpdateChatRequest updateChatRequest, UUID userId, Pageable pageable);
}
