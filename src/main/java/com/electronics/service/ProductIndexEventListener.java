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

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductIndexEvent(ProductIndexEvent event) {
        log.debug("Handling index event: productId={}, action={}", event.productId(), event.action());
        switch (event.action()) {
            case INDEX -> productIndexService.indexProduct(event.productId());
            case REMOVE -> productIndexService.removeProduct(event.productId());
        }
    }
}
