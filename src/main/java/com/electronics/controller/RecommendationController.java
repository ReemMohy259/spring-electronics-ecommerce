package com.electronics.controller;

import com.electronics.dto.RecommendationRequest;
import com.electronics.dto.RecommendationResponse;
import com.electronics.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    public List<RecommendationResponse> recommend(
            @Valid @RequestBody RecommendationRequest request) {
        return recommendationService.recommend(request);
    }
}
