package org.d1ff.messageservice.grpc;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChatGrpcClient {
    public boolean isUserExistsInChat(UUID userId, UUID chatId){
        return true;
    }
    public List<UUID> getChatParticipants(UUID chatId){
        return List.of(UUID.randomUUID(),UUID.randomUUID(),UUID.randomUUID());
    }
}
