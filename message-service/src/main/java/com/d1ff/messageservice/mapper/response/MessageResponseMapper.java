package com.d1ff.messageservice.mapper.response;

import com.d1ff.dto.response.PresignedUrlResponse;
import com.d1ff.messageservice.dto.response.MessageResponse;
import com.d1ff.messageservice.entity.Message;
import com.d1ff.grpc.client.FileGrpcClient;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)

public interface MessageResponseMapper {
    @Mapping(target = "fileUrl", ignore = true)
    @Mapping(target = "fileUrlTtl", ignore = true)
    @Mapping(target = "replyTo", expression = "java(message.getReplyTo() != null ? message.getReplyTo().getId() : null)")
    MessageResponse toMessageResponse(Message message);

    @Mapping(target = "fileUrl", expression = "java(resolveFileUrl(message, fileService))")
    @Mapping(target = "fileUrlTtl", expression = "java(resolveFileTtl(message, fileService))")
    @Mapping(target = "replyTo", expression = "java(message.getReplyTo() != null ? message.getReplyTo().getId() : null)")
    MessageResponse toMessageResponseWithUrl(Message message, @Context FileGrpcClient fileService);

    default PresignedUrlResponse resolveFile(Message message, FileGrpcClient fileService) {
        if (message.getBucketName() == null || message.getObjectName() == null) return null;
        return fileService.getPresignedUrl(message.getBucketName(), message.getObjectName());
    }

    default String resolveFileUrl(Message message, FileGrpcClient fileService) {
        PresignedUrlResponse resp = resolveFile(message, fileService);
        return resp != null ? resp.url() : null;
    }

    default Long resolveFileTtl(Message message, FileGrpcClient fileService) {
        PresignedUrlResponse resp = resolveFile(message, fileService);
        return resp != null ? resp.ttl() : null;
    }
}