package com.d1ff.chatservice.service.interfaces;

import com.d1ff.chatservice.dto.request.AddParticipantRequest;
import com.d1ff.chatservice.dto.request.KickParticipantRequest;
import com.d1ff.chatservice.dto.request.UpdateParticipantStatusRequest;
import com.d1ff.chatservice.dto.response.ChatParticipantResponse;
import com.d1ff.chatservice.dto.response.ChatResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface ChatParticipantService {
    //TODO: агрегированый запрос через gateway чтобы сразу найти по айди юзера
    @Transactional
    ChatParticipantResponse updateUserRole(UpdateParticipantStatusRequest participantStatusRequest, UUID userId);

    @Transactional
    ChatResponse addParticipant(UUID initiatorId, UUID chatId, AddParticipantRequest addParticipantRequest, Pageable pageable);

    @Transactional
    void kickParticipant(UUID userId, KickParticipantRequest kickParticipantRequest);
}
