package com.d1ff.fulltextsearchservice.mapper;
import com.d1ff.common.avro.MessageSentEvent;
import com.d1ff.fulltextsearchservice.entity.document.MessageDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface MessageEventMapper {
    @Mapping(target = "messageId", expression = "java(String.valueOf(messageEvent.getMessageId()))")
    @Mapping(target = "chatId", expression = "java(String.valueOf(messageEvent.getChatId()))")
    @Mapping(target = "text", expression = "java(messageEvent.getContent() == null ? \"\" : messageEvent.getContent())")
    @Mapping(target = "createdAt", source = "timestamp")
    @Mapping(target = "isDeleted", constant = "false")
    MessageDocument toMessageDocument(MessageSentEvent messageEvent);
}
