package com.electronics.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProductsBlock extends ContentBlock {

    private List<ProductCard> products;

    private String label;

    @Override
    public String getType() {
        return "products";
    }
}
