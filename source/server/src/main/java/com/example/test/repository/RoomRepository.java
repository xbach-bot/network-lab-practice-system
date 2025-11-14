package com.example.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.test.domain.Room;

public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    @Query("select r from Room r join r.participants p1 join r.participants p2 " +
            "where p1.id = :userId1 and p2.id = :userId2 and size(r.participants) = 2")
    Room findPrivateRoomBetween(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

}
