package com.ecommerce.e_commerce.core.common.exception;

import com.ecommerce.e_commerce.core.product.dtos.StockWarning;
import lombok.Getter;

import java.util.List;

import static com.ecommerce.e_commerce.core.common.utils.Constants.STOCK_ISSUES;

@Getter
public class InsufficientStockException extends RuntimeException {
    private final List<StockWarning> stockIssues;

    public InsufficientStockException(List<StockWarning> stockIssues) {
        super(STOCK_ISSUES);
        this.stockIssues = stockIssues;
    }
}
