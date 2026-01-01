package com.ecommerce.e_commerce.security.auth.model;

import com.ecommerce.e_commerce.security.token.model.Token;
import com.ecommerce.e_commerce.user.profile.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "auth_users", schema = "e-commerce")
public class AuthUser implements UserDetails {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @OneToMany(mappedBy = "authUser")
    private Set<Token> tokens;
    @OneToOne
    @MapsId // Tells JPA that this entity shares its PK with 'user'
    @JoinColumn(name = "id") // Maps to the same column
    private User user;
    @Column(name = "last_login")
    private Instant lastLogin;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "auth_user_roles",
            joinColumns = @JoinColumn(name = "auth_user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )

    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleEnum().name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
