package com.ecommerce.e_commerce.auth.token.repository;

import com.ecommerce.e_commerce.auth.auth.model.AuthUser;
import com.ecommerce.e_commerce.auth.token.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);

    Optional<Token> findByAuthUser(AuthUser authUser);

    @Query(value = """
      select t from Token t inner join AuthUser u
      on t.authUser.id = u.id
      where u.id = :id and (t.expired = false or t.revoked = false)
      """)
    List<Token> findAllValidTokensByUser(Long id);
}
