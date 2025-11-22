package com.ecommerce.e_commerce.core.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Builder
public class ReviewResponse {
    private Integer reviewId;
    private Integer productId;
    private String productName;
    private Integer userId;
    private String userName;
    private Integer rating;
    private String title;
    private String comment;
    private Instant createdAt;
}
