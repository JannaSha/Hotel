package com.users.repos;

import com.users.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository  extends CrudRepository<User, Long> {
    List<User> findAll(Pageable pageable);
    User findByPassportNumber(long passportNumber);
    User findByUsername(String username);
}
