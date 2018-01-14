package com.auth.repos;

import com.auth.model.Auth;
import org.springframework.data.repository.CrudRepository;

public interface AuthRepository  extends CrudRepository<Auth, Long> {
    Auth findByUsername(String username);

}
