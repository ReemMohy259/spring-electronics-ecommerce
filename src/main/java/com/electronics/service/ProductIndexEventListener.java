package com.electronics.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductIndexEventListener {

    private final ProductIndexService productIndexService;
    private final ProductIngestionService ingestionService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductIndexEventForElasticSearch(ProductIndexEvent event) {
        log.info("[{}] Indexing product with id = {} for elastic search.", event.productId(), Thread.currentThread().threadId());
        log.debug(
            "Handling index event: productId={}, action={} for elastic search",
            event.productId(),
            event.action());
        switch (event.action()) {
            case INDEX -> productIndexService.indexProduct(event.productId());
            case REMOVE -> productIndexService.removeProduct(event.productId());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductIndexEventForEmbeddingModel(ProductIndexEvent event) {
        log.info("[{}] Indexing product with id = {} for embedding model.", event.productId(), Thread.currentThread().threadId());
        log.debug(
            "Handling index event: productId={}, action={} for embedding model",
            event.productId(),
            event.action());
        switch (event.action()) {
            case INDEX -> ingestionService.indexProduct(event.productId());
            case REMOVE -> productIndexService.removeProduct(event.productId());
        }
    }
}
