package com.d1ff.fulltextsearchservice.repository;

import com.d1ff.fulltextsearchservice.entity.document.MessageDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageDocumentRepository extends ElasticsearchRepository<MessageDocument, String> {
}
