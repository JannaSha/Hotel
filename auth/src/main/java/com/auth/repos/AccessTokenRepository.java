package com.auth.repos;

import com.auth.model.AccessToken;
import org.springframework.data.repository.CrudRepository;

public interface AccessTokenRepository extends CrudRepository<AccessToken, Long>{
    AccessToken findByUsername(String username);
    AccessToken findByAccessTokenAndScope(String token, String scope);
}
