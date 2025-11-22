package com.ecommerce.e_commerce.core.cart.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode
@Embeddable
public class CartItemId implements Serializable {
    private Long cart;
    private Long product;
}
