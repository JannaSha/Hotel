package com.rooms.repo;

import com.rooms.models.RoomType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoomsTypesRepository extends CrudRepository<RoomType, Long> {
    List<RoomType> findAll(Pageable pageable);
}
