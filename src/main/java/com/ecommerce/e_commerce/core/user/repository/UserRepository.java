package com.ecommerce.e_commerce.core.user.repository;

import com.ecommerce.e_commerce.auth.auth.model.AuthUser;
import com.ecommerce.e_commerce.core.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

}
