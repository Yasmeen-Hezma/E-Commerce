package com.ecommerce.e_commerce.security.auth.model;

import com.ecommerce.e_commerce.security.auth.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles", schema = "e-commerce")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false)
    private Long roleId;


    @Enumerated(value = EnumType.STRING)
    @Column(name = "role_name")
    private RoleEnum roleEnum;

    @ManyToMany(mappedBy = "roles")
    private Set<AuthUser> users;
}
