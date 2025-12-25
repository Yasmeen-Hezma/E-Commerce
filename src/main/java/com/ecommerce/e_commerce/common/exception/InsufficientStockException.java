package com.ecommerce.e_commerce.common.exception;

import com.ecommerce.e_commerce.commerce.product.dto.StockWarning;
import lombok.Getter;

import java.util.List;

import static com.ecommerce.e_commerce.common.utils.Constants.STOCK_ISSUES;

@Getter
public class InsufficientStockException extends RuntimeException {
    private final List<StockWarning> stockIssues;

    public InsufficientStockException(List<StockWarning> stockIssues) {
        super(STOCK_ISSUES);
        this.stockIssues = stockIssues;
    }
}
