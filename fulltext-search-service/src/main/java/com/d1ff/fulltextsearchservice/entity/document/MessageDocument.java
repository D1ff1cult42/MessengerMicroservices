package com.d1ff.fulltextsearchservice.entity.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "messages")
@Setting(settingPath = "elasticsearch-settings.json")
public class MessageDocument {
    @Id
    private String messageId;

    @Field(type = FieldType.Keyword)
    private String chatId;

    @Field(type = FieldType.Text, analyzer = "ru_analyzer")
    private String text;

    @Field(type = FieldType.Date)
    private Instant createdAt;

    @Field(type = FieldType.Boolean)
    private Boolean isDeleted;
}
