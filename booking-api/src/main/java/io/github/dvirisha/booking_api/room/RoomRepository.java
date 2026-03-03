package io.github.dvirisha.booking_api.room;

import io.github.dvirisha.booking_api.room.dto.GetRoomFilter;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    boolean existsByNameIgnoreCase(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Room r where r.id = ?1")
    Optional<Room> lockById(Long id);

    @Query("select r from Room r where r.capacity > ?1")
    List<Room> findAll(Integer capacityMin);

}
