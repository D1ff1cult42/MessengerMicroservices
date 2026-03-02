package com.d1ff.fulltextsearchservice.grpc;

import com.d1ff.common.grpc.chat.ChatGrpcServiceGrpc;
import com.d1ff.common.grpc.chat.IsUserExistsInChatRequest;
import com.d1ff.common.grpc.chat.IsUserExistsInChatResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class GrpcChatClient {

    @GrpcClient("chat-service")
    private ChatGrpcServiceGrpc.ChatGrpcServiceBlockingStub blockingStub;

    public boolean isUserExistsInChat(UUID userId, UUID chatId) {
        log.info("Checking if user {} exists in chat {}", userId, chatId);

        try {
            IsUserExistsInChatRequest request = IsUserExistsInChatRequest.newBuilder()
                    .setChatId(chatId.toString())
                    .setUserId(userId.toString())
                    .build();
            IsUserExistsInChatResponse response = blockingStub.isUserExistsInChat(request);
            log.info("User {} exists in chat {}: {}", userId, chatId, response.getUserIsExists());
            return response.getUserIsExists();
        } catch (StatusRuntimeException ex) {
            handleGrpcException(ex, "gRPC error checking user in chat");
            throw new RuntimeException("This should never happen");
        }
    }

    private void handleGrpcException(StatusRuntimeException e, String message) {
        log.error("{} Status: {}, Description: {}",
                message, e.getStatus().getCode(), e.getStatus().getDescription(), e);

        Status.Code statusCode = e.getStatus().getCode();
        String description = e.getStatus().getDescription();

        switch (statusCode) {
            case UNAVAILABLE:
                throw new RuntimeException("Chat service is unavailable. Please try again later.", e);

            case DEADLINE_EXCEEDED:
                throw new RuntimeException("Chat service timeout. Please try again.", e);

            case INVALID_ARGUMENT:
                throw new IllegalArgumentException("Invalid request: " + description, e);

            case NOT_FOUND:
                throw new IllegalArgumentException("Chat not found: " + description, e);

            case FAILED_PRECONDITION:
                throw new IllegalStateException("Chat is not available: " + description, e);

            case INTERNAL:
                throw new RuntimeException("Internal error in chat service: " + description, e);

            default:
                throw new RuntimeException("Failed to call chat service: " + description, e);
        }
    }
}
