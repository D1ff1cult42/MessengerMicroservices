package com.d1ff.chatservice.mapper.response;

import com.d1ff.chatservice.dto.response.ChatParticipantResponse;
import com.d1ff.chatservice.entity.ChatParticipant;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ChatParticipantResponseMapper {
    ChatParticipantResponse toChatParticipantResponse(ChatParticipant chatParticipant);
}
