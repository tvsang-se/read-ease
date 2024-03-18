package com.readease.userservice.Repo;

import com.readease.userservice.Model.Token;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TokenRepo extends JpaRepository<Token, Integer> {

   Optional<Token> findTokenByToken(String token);
   @Query(value = "SELECT userID FROM token WHERE token = ?1", nativeQuery = true)
   String findUserIDByToken(String token);

   @Query(value = "SELECT token FROM token WHERE userID = ?1 and type = 'ACCESS' ", nativeQuery = true)
   String findAccessTokenByUserID(String userID);

   @Transactional
   @Modifying
   @Query(value = "DELETE FROM token t\n" +
           "WHERE t.userID = ?1", nativeQuery = true)
   void deleteTokenByUserID(String userID);

   @Query(value = "SELECT * FROM token WHERE userID = ?1 AND type = ?2", nativeQuery = true)
   Optional <Token> findTokenByUserIDAndType(String id,String tokenType);

   @Query(value = "SELECT * FROM token WHERE token.type = ?1", nativeQuery = true)
   Optional <Token> findGGToken(String tokenType);
}
