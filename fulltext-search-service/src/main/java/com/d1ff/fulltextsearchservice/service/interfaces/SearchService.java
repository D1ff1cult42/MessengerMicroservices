package com.d1ff.fulltextsearchservice.service.interfaces;

import com.d1ff.fulltextsearchservice.dto.request.MessageSearchRequest;
import com.d1ff.fulltextsearchservice.dto.response.MessageSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SearchService {
    Page<MessageSearchResponse> search(MessageSearchRequest searchRequest, UUID userId, Pageable pageable);
}
