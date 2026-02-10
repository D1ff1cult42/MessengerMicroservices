package org.d1ff.messageservice.mapper.response;

import org.d1ff.messageservice.dto.response.MessageResponse;
import org.d1ff.messageservice.entity.Message;
import org.d1ff.messageservice.service.interfaces.FileService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)

public interface MessageResponseMapper {
    @Mapping(target = "fileUrl", ignore = true)
    @Mapping(target = "replyTo", expression = "java(message.getReplyTo() != null ? message.getReplyTo().getId() : null)")
    MessageResponse toMessageResponse(Message message);

    @Mapping(target = "fileUrl", expression = "java(message.getBucketName() == null || message.getObjectName() == null ? null : fileService.generatePresignedUrl(new org.d1ff.messageservice.dto.MinioFileProperties(message.getBucketName(), message.getObjectName())))")
    @Mapping(target = "replyTo", expression = "java(message.getReplyTo() != null ? message.getReplyTo().getId() : null)")
    MessageResponse toMessageResponseWithUrl(Message message, @Context FileService fileService);
}