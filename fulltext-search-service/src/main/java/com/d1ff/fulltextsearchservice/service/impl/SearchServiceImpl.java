package com.d1ff.fulltextsearchservice.service.impl;


import com.d1ff.fulltextsearchservice.dto.request.MessageSearchRequest;
import com.d1ff.fulltextsearchservice.dto.response.MessageSearchResponse;
import com.d1ff.fulltextsearchservice.entity.document.MessageDocument;
import com.d1ff.fulltextsearchservice.exceptions.AccessDeniedException;
import com.d1ff.grpc.client.chat.ChatGrpcClient;
import com.d1ff.fulltextsearchservice.service.interfaces.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchOperations operations;
    private final ChatGrpcClient chatGrpcClient;

    @Override
    public Page<MessageSearchResponse> search(MessageSearchRequest searchRequest, UUID userId, Pageable pageable) {
        log.info("Searching messages in chat {} by user {}, query='{}', page={}, size={}",
                searchRequest.chatId(), userId, searchRequest.query(),
                pageable.getPageNumber(), pageable.getPageSize());

        if (!chatGrpcClient.isUserExistsInChat(userId, searchRequest.chatId())) {
            log.warn("Access denied: user {} is not a member of chat {}", userId, searchRequest.chatId());
            throw new AccessDeniedException("User does not have access to this chat");
        }

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.
                        bool(b -> b.
                                must(m -> m.
                                        match(mm -> mm
                                                .field("text")
                                                .query(searchRequest.query())
                                                .fuzziness("AUTO")
                                        )
                                )
                                .filter(f -> f
                                        .term(t -> t
                                                .field("chatId")
                                                .value(searchRequest.chatId().toString()))
                                        )
                                .filter(f -> f
                                        .term(t -> t.field("isDeleted")
                                                .value(false))
                                )
                        )
                )
                .withPageable(pageable)
                .build();

        SearchHits<MessageDocument> hits =
                operations.search(query, MessageDocument.class);

        log.debug("Elasticsearch returned {} hits (total: {}) for query '{}' in chat {}",
                hits.getSearchHits().size(), hits.getTotalHits(),
                searchRequest.query(), searchRequest.chatId());

        List<MessageSearchResponse> content = hits.stream()
                .map(hit -> new MessageSearchResponse(
                        hit.getContent().getMessageId(),
                        hit.getContent().getChatId(),
                        hit.getContent().getText(),
                        hit.getScore()
                ))
                .toList();

        log.info("Search completed: {} results returned for chat {}", content.size(), searchRequest.chatId());
        return new PageImpl<>(content, pageable, hits.getTotalHits());
    }
}
