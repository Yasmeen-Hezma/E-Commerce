package com.ecommerce.e_commerce.commerce.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Builder
public class ReviewResponse {
    private Long reviewId;
    private Long productId;
    private String productName;
    private Long userId;
    private String userName;
    private Integer rating;
    private String title;
    private String comment;
    private Instant createdAt;
}
