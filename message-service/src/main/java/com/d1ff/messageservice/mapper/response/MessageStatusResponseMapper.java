package com.d1ff.messageservice.mapper.response;

import com.d1ff.messageservice.dto.response.MessageStatusResponse;
import com.d1ff.messageservice.dto.response.MessageStatusWithoutMessageResponse;
import com.d1ff.messageservice.entity.MessageStatus;
import com.d1ff.grpc.client.file.FileGrpcClient;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE
)
public interface MessageStatusResponseMapper {
    @Mapping(source = "readAt", target = "date")
    @Mapping(target = "message", expression = "java(messageResponseMapper.toMessageResponseWithUrl(messageStatus.getMessage(), fileService))")
    MessageStatusResponse toMessageStatusResponse(
            MessageStatus messageStatus,
            @Context MessageResponseMapper messageResponseMapper,
            @Context FileGrpcClient fileService
    );

    @Mapping(source = "readAt", target = "date")
    @Mapping(target = "message", expression = "java(status.getMessage().getId())")
    MessageStatusWithoutMessageResponse toMessageStatusResponseWithoutMessage(MessageStatus status);

    @Mapping(source = "readAt", target = "date")
    @Mapping(target = "message", expression = "java(messageResponseMapper.toMessageResponse(messageStatus.getMessage()))")
    MessageStatusResponse toMessageStatusResponseWithoutUrl(
            MessageStatus messageStatus,
            @Context MessageResponseMapper messageResponseMapper
    );
}
