package com.electronics.service;

import com.electronics.dto.MerchantResponse;
import com.electronics.entity.Merchant;
import com.electronics.exception.MerchantNotFoundException;
import com.electronics.repository.MerchantRepository;
import com.electronics.repository.ProductRepository;
import com.electronics.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public MerchantResponse findById(Integer id) {
        Merchant merchant = merchantRepository.findById(id)
            .orElseThrow(() -> new MerchantNotFoundException(id));

        Long totalProducts = productRepository.countByMerchantIdAndDeletedFalse(id);
        Double rating = reviewRepository.findAverageRatingByMerchantId(id);

        return new MerchantResponse(
            merchant.getId(),
            merchant.getEmail(),
            merchant.getProfilePicUrl(),
            rating,
            totalProducts,
            merchant.getCreatedAt());
    }
}
