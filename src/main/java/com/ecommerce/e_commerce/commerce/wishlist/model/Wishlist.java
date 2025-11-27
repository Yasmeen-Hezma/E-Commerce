package com.ecommerce.e_commerce.commerce.wishlist.model;

import com.ecommerce.e_commerce.user.profile.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wishlists", schema = "e-commerce")
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_id", nullable = false)
    private Long wishlistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<WishlistItem> wishlistItems = new ArrayList<>();

    public void addWishlistItem(WishlistItem item) {
        wishlistItems.add(item);
        item.setWishlist(this);
    }

    public void clearItems() {
        wishlistItems.clear();
    }

    public void setWishlistItems(List<WishlistItem> newItems) {
        this.wishlistItems.clear();
        if (newItems != null) {
            newItems.forEach(this::addWishlistItem);
        }
    }
}
