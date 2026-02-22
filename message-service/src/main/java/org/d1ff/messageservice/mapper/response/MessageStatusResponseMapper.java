package org.d1ff.messageservice.mapper.response;

import org.d1ff.messageservice.dto.response.MessageStatusResponse;
import org.d1ff.messageservice.dto.response.MessageStatusWithoutMessageResponse;
import org.d1ff.messageservice.entity.MessageStatus;
import org.d1ff.grpc.client.FileGrpcClient;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE
)
public interface MessageStatusResponseMapper {
    @Mapping(target = "message", expression = "java(messageResponseMapper.toMessageResponseWithUrl(messageStatus.getMessage(), fileService))")
    MessageStatusResponse toMessageStatusResponse(
            MessageStatus messageStatus,
            @Context MessageResponseMapper messageResponseMapper,
            @Context FileGrpcClient fileService
    );

    @Mapping(target = "message", expression = "java(status.getMessage().getId())")
    MessageStatusWithoutMessageResponse toMessageStatusResponseWithoutMessage(MessageStatus status);

    @Mapping(target = "message", expression = "java(messageResponseMapper.toMessageResponse(messageStatus.getMessage()))")
    MessageStatusResponse toMessageStatusResponseWithoutUrl(
            MessageStatus messageStatus,
            @Context MessageResponseMapper messageResponseMapper
    );
}
