package com.ecommerce.e_commerce.security.auth.repository;

import com.ecommerce.e_commerce.security.auth.model.Role;
import com.ecommerce.e_commerce.security.auth.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleEnum(RoleEnum roleEnum);
}
