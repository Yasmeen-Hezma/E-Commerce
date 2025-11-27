package com.ecommerce.e_commerce.user.profile.repository;

import com.ecommerce.e_commerce.user.profile.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

}
