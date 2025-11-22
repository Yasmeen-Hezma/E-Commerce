package com.ecommerce.e_commerce.core.brand.model;

import com.ecommerce.e_commerce.core.cart.model.CartItem;
import com.ecommerce.e_commerce.core.product.model.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@Table(name = "brands", schema = "e-commerce")
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id", nullable = false)
    private Long brandId;

    @NotBlank
    @Size(min = 3, message = "brand name must contains at least 3 characters")
    @Column(name = "brand_name", nullable = false)
    private String brandName;

    @Builder.Default
    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "image")
    private String image;
}
