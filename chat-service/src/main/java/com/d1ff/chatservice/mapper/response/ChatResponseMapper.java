package com.d1ff.chatservice.mapper.response;

import com.d1ff.chatservice.dto.response.ChatResponse;
import com.d1ff.chatservice.entity.Chat;
import com.d1ff.chatservice.repository.ChatParticipantRepository;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ChatResponseMapper {
    @Mappings({
            @Mapping(target = "participants", expression = "java(chatParticipantRepository.findByChatId(chat.getId()).map(participantMapper::toChatParticipantResponse))")
    })
    ChatResponse toChatResponse(Chat chat,
                                @Context ChatParticipantResponseMapper participantMapper,
                                @Context ChatParticipantRepository chatParticipantRepository);
}
