package com.electronics.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.electronics.document.ProductDocument;
import com.electronics.dto.ProductDto;
import com.electronics.dto.ProductSearchRequest;
import com.electronics.dto.ProductSearchResponse;
import com.electronics.dto.ProductSort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public ProductSearchResponse search(ProductSearchRequest request) {
        NativeQuery query = buildQuery(request);
        SearchHits<ProductDocument> hits = elasticsearchOperations.search(query, ProductDocument.class);

        List<ProductDto> products = hits.getSearchHits().stream()
            .map(hit -> ProductDto.from(hit.getContent()))
            .toList();

        return ProductSearchResponse.builder()
            .products(products)
            .totalElements(hits.getTotalHits())
            .currentPage(request.getPage())
            .totalPages((int) Math.ceil((double) hits.getTotalHits() / request.getSize()))
            .build();
    }

    public List<String> autocomplete(String query) {
        NativeQuery nativeQuery = NativeQuery.builder()
            .withQuery(q -> q.matchPhrasePrefix(m -> m
                .field("name")
                .query(query)
            ))
            .withMaxResults(10)
            .build();

        return elasticsearchOperations.search(nativeQuery, ProductDocument.class)
            .getSearchHits()
            .stream()
            .map(hit -> hit.getContent().getName())
            .distinct()
            .toList();
    }

    private NativeQuery buildQuery(ProductSearchRequest request) {
        BoolQuery.Builder bool = new BoolQuery.Builder();

        if (request.getQuery() != null && !request.getQuery().isBlank()) {
            bool.must(m -> m.multiMatch(mm -> mm
                .query(request.getQuery())
                .fields("name^3", "description")
                .fuzziness("AUTO")
            ));
        }

        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            bool.filter(f -> f.terms(t -> t
                .field("categories")
                .terms(v -> v.value(
                    request.getCategories().stream()
                        .map(FieldValue::of)
                        .toList()
                ))
            ));
        }

        if (request.getMinPrice() != null || request.getMaxPrice() != null) {
            bool.filter(f -> f.range(r -> r.number(n -> {
                n.field("price");
                if (request.getMinPrice() != null) {
                    n.gte(request.getMinPrice().doubleValue());
                }
                if (request.getMaxPrice() != null) {
                    n.lte(request.getMaxPrice().doubleValue());
                }
                return n;
            })));
        }

        if (request.getMinRating() != null) {
            bool.filter(f -> f.range(r -> r.number(n -> n
                .field("rating")
                .gte(request.getMinRating())
            )));
        }

        BoolQuery boolQuery = bool.build();
        Query query = boolQuery._toQuery();

        NativeQueryBuilder builder = NativeQuery.builder()
            .withQuery(query);

        builder.withPageable(PageRequest.of(request.getPage(), request.getSize()));
        applySorting(builder, request.getSort());

        return builder.build();
    }

    private void applySorting(NativeQueryBuilder builder, ProductSort sort) {
        if (sort == null) return;

        switch (sort) {
            case PRICE_ASC ->
                builder.withSort(Sort.by(Sort.Order.asc("price")));
            case PRICE_DESC ->
                builder.withSort(Sort.by(Sort.Order.desc("price")));
            case TOP_RATED ->
                builder.withSort(Sort.by(Sort.Order.desc("rating")));
            case LATEST ->
                builder.withSort(Sort.by(Sort.Order.desc("createdAt")));
            default -> {
            }
        }
    }
}
