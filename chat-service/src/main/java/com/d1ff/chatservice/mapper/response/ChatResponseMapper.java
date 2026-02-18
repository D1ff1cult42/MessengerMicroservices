package com.d1ff.chatservice.mapper.response;

import com.d1ff.chatservice.dto.response.ChatResponse;
import com.d1ff.chatservice.entity.Chat;
import com.d1ff.chatservice.repository.ChatParticipantRepository;
import org.d1ff.page.PageResponse;
import org.mapstruct.*;
import org.springframework.data.domain.Pageable;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ChatResponseMapper {
    @Mappings({
            @Mapping(target = "participants", expression = "java(PageResponse.fromPage(chatParticipantRepository.findByChatId(chat.getId(), pageable).map(participantMapper::toChatParticipantResponse)))")
    })
    ChatResponse toChatResponse(Chat chat,
                                @Context Pageable pageable,
                                @Context ChatParticipantResponseMapper participantMapper,
                                @Context ChatParticipantRepository chatParticipantRepository);
}
