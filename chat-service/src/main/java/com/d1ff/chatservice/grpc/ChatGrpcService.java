package com.d1ff.chatservice.grpc;

import com.d1ff.chatservice.entity.ChatParticipant;
import com.d1ff.chatservice.repository.ChatParticipantRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.d1ff.common.grpc.file.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@GrpcService
public class ChatGrpcService extends ChatGrpcServiceGrpc.ChatGrpcServiceImplBase {

    private final ChatParticipantRepository chatParticipantRepository;

    @Override
    public void isUserExistsInChat(IsUserExistsInChatRequest request, StreamObserver<IsUserExistsInChatResponse> observer) {
        try {
            log.info("gRPC isUserExistsInChat called with chatId: {}, userId: {}", request.getChatId(), request.getUserId());

            if (request.getChatId().isEmpty() || request.getUserId().isEmpty()) {
                log.warn("Empty data received");
                observer.onError(Status.INVALID_ARGUMENT
                        .withDescription("Data cannot be empty")
                        .asRuntimeException());
                return;
            }

            ChatParticipant chatParticipant = chatParticipantRepository
                    .findByUserIdAndChatId(UUID.fromString(request.getUserId()),
                            UUID.fromString(request.getChatId()))
                    .orElse(null);

            if (chatParticipant != null && chatParticipant.getChat().isDeleted()) {
                observer.onError(Status.NOT_FOUND
                        .withDescription("Chat Not Found")
                        .asRuntimeException());
                return;
            }

            IsUserExistsInChatResponse response = IsUserExistsInChatResponse.newBuilder()
                    .setUserIsExists(chatParticipant != null)
                    .build();

            log.info("Returning {} value from gRPC", response.getUserIsExists());

            observer.onNext(response);
            observer.onCompleted();
        } catch (Exception ex) {
            log.error("Error getting userExists from gRPC chat-service: {}", ex.getMessage());
            observer.onError(Status.INTERNAL
                    .withDescription("Error getting userExists from gRPC chat-service: " + ex.getMessage())
                    .withCause(ex)
                    .asRuntimeException());
        }
    }

    @Override
    public void getChatParticipants(GetChatParticipantsRequest request, StreamObserver<GetChatParticipantsResponse> observer) {
        try {
            log.info("gRPC getChatParticipants called with chatId: {}", request.getChatId());

            if (request.getChatId().isEmpty()) {
                log.warn("Empty chatId received");
                observer.onError(Status.INVALID_ARGUMENT
                        .withDescription("Chat ID cannot be empty")
                        .asRuntimeException());
                return;
            }

            UUID chatId = UUID.fromString(request.getChatId());

            ChatParticipant anyParticipant = chatParticipantRepository.findFirstByChatId(chatId)
                    .orElse(null);

            if (anyParticipant == null) {
                observer.onError(Status.NOT_FOUND
                        .withDescription("Chat not found: " + chatId)
                        .asRuntimeException());
                return;
            }

            if (anyParticipant.getChat().isDeleted()) {
                observer.onError(Status.NOT_FOUND
                        .withDescription("Chat not found: " + chatId)
                        .asRuntimeException());
                return;
            }

            List<UUID> userIds = chatParticipantRepository.findUserIdsByChatId(chatId);

            GetChatParticipantsResponse response = GetChatParticipantsResponse.newBuilder()
                    .addAllIds(userIds.stream().map(UUID::toString).toList())
                    .build();

            log.info("Returning {} participants for chat {}", userIds.size(), chatId);

            observer.onNext(response);
            observer.onCompleted();
        } catch (Exception ex) {
            log.error("Error getting chat participants from gRPC chat-service: {}", ex.getMessage());
            observer.onError(Status.INTERNAL
                    .withDescription("Error getting chat participants from gRPC chat-service: " + ex.getMessage())
                    .withCause(ex)
                    .asRuntimeException());
        }
    }
}

