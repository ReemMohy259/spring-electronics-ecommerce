package com.electronics.controller;

import com.electronics.dto.MerchantResponse;
import com.electronics.dto.MerchantSummaryResponse;
import com.electronics.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @GetMapping("/summary")
    public ResponseEntity<List<MerchantSummaryResponse>> getAllSummary() {
        return ResponseEntity.ok(merchantService.findAllSummary());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MerchantResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(merchantService.findById(id));
    }
}
