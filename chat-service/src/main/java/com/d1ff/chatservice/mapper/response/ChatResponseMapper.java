package com.d1ff.chatservice.mapper.response;

import com.d1ff.chatservice.dto.response.ChatResponse;
import com.d1ff.chatservice.entity.Chat;
import com.d1ff.chatservice.repository.ChatParticipantRepository;
import com.d1ff.dto.response.PresignedUrlResponse;
import com.d1ff.grpc.client.file.FileGrpcClient;
import com.d1ff.page.PageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Pageable;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ChatResponseMapper {

    default ChatResponse toChatResponse(Chat chat,
                                        Pageable pageable,
                                        ChatParticipantResponseMapper participantMapper,
                                        ChatParticipantRepository chatParticipantRepository,
                                        FileGrpcClient fileService) {
        if (chat == null) return null;

        PresignedUrlResponse icon = null;
        if (chat.getIconBucketName() != null && chat.getIconObjectName() != null) {
            icon = fileService.getPresignedUrl(chat.getIconBucketName(), chat.getIconObjectName());
        }

        return new ChatResponse(
                chat.getId(),
                chat.getName(),
                chat.getDescription(),
                chat.getCreatedAt(),
                icon != null ? icon.url() : null,
                icon != null ? icon.ttl() : null,
                chat.isGroupChat(),
                PageResponse.fromPage(
                        chatParticipantRepository.findByChatId(chat.getId(), pageable)
                                .map(participantMapper::toChatParticipantResponse))
        );
    }
}
