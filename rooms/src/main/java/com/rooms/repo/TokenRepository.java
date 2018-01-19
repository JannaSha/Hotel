package com.rooms.repo;

import com.rooms.models.Token;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TokenRepository extends CrudRepository<Token, Long>{
    List<Token> findAll();
//    Token findTop();

}
