package com.d1ff.chatservice.mapper.response;

import com.d1ff.chatservice.dto.response.ChatResponse;
import com.d1ff.chatservice.entity.Chat;
import org.d1ff.dto.response.PresignedUrlResponse;
import org.d1ff.grpc.client.FileGrpcClient;
import com.d1ff.chatservice.repository.ChatParticipantRepository;
import org.d1ff.page.PageResponse;
import org.mapstruct.*;
import org.springframework.data.domain.Pageable;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ChatResponseMapper {
    @Mappings({
            @Mapping(target = "participants", expression = "java(PageResponse.fromPage(chatParticipantRepository.findByChatId(chat.getId(), pageable).map(participantMapper::toChatParticipantResponse)))"),
            @Mapping(target = "presignedIconUrl", expression = "java(resolveIconUrl(chat, fileService))"),
            @Mapping(target = "iconUrlTtl", expression = "java(resolveIconTtl(chat, fileService))")
    })
    ChatResponse toChatResponse(Chat chat,
                                @Context Pageable pageable,
                                @Context ChatParticipantResponseMapper participantMapper,
                                @Context ChatParticipantRepository chatParticipantRepository,
                                @Context FileGrpcClient fileService);

    default PresignedUrlResponse resolveIcon(Chat chat, FileGrpcClient fileService) {
        if (chat.getIconBucketName() == null || chat.getIconObjectName() == null) return null;
        return fileService.getPresignedUrl(chat.getIconBucketName(), chat.getIconObjectName());
    }

    default String resolveIconUrl(Chat chat, FileGrpcClient fileService) {
        PresignedUrlResponse resp = resolveIcon(chat, fileService);
        return resp != null ? resp.url() : null;
    }

    default Long resolveIconTtl(Chat chat, FileGrpcClient fileService) {
        PresignedUrlResponse resp = resolveIcon(chat, fileService);
        return resp != null ? resp.ttl() : null;
    }
}
