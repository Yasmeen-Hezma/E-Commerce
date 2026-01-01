package com.ecommerce.e_commerce.user.profile.model;

import com.ecommerce.e_commerce.commerce.wishlist.model.Wishlist;
import com.ecommerce.e_commerce.security.auth.enums.SellerType;
import com.ecommerce.e_commerce.security.auth.model.AuthUser;
import com.ecommerce.e_commerce.commerce.cart.model.Cart;
import com.ecommerce.e_commerce.commerce.order.model.Order;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", schema = "e-commerce")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    // ========== BASIC USER INFO ==========
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "phone", unique = true)
    private String phone;
    @Column(name = "phone_code")
    private Integer phoneCode;
    // ========== DEFAULT SHIPPING ADDRESS (For Customers) ==========
    @Column(name = "governorate")
    private String governorate;
    @Column(name = "city")
    private String city;
    @Column(name = "street")
    private String street;
    @Column(name = "floor_number")
    private String floorNumber;
    @Column(name = "apartment_number")
    private String apartmentNumber;
    // ========== SELLER-SPECIFIC FIELDS ==========
    @Column(name = "brand_name")
    private String brandName;
    @Enumerated(EnumType.STRING)
    private SellerType sellerType;
    @Column(name = "shipping_zone")
    private String shippingZone;
    @Column(name = "business_address")
    private String businessAddress;
    // ========== RELATIONSHIPS ==========
    @OneToOne(mappedBy = "user")
    private AuthUser authUser;
    @OneToOne(mappedBy = "user")
    private Cart cart;
    @OneToOne(mappedBy = "user")
    private Wishlist wishlist;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    public boolean hasCompleteShippingAddress() {
        return governorate != null
                && city != null
                && street != null
                && phone != null;
    }
}
