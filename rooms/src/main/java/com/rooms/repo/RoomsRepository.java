package com.rooms.repo;


import com.rooms.models.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoomsRepository extends CrudRepository<Room, Long> {
    List<Room> findByRoomType(long id);
    List<Room> findAll(Pageable pageable);
    List<Room> findByRoomTypeAndVacant(long roomType, boolean vacant);
}
