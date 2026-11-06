package com.hotel.booking.repository;

import com.hotel.booking.domain.entity.Room;
import com.hotel.booking.domain.enums.RoomType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByPropertyId(Long propertyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT r FROM Room r
            WHERE r.property.id = :propertyId
              AND r.roomType = :roomType
              AND r.capacity >= :numGuests
              AND r.available = true
              AND r.id NOT IN (
                  SELECT b.room.id FROM Booking b
                  WHERE b.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN')
                    AND b.dateRange.checkInDate  < :checkOut
                    AND b.dateRange.checkOutDate > :checkIn
              )
            """)
    List<Room> findAvailableRooms(@Param("propertyId") Long propertyId,
                                   @Param("roomType") RoomType roomType,
                                   @Param("numGuests") int numGuests,
                                   @Param("checkIn") LocalDate checkIn,
                                   @Param("checkOut") LocalDate checkOut);

    @Query("""
            SELECT r FROM Room r
            WHERE r.property.id = :propertyId
              AND r.capacity >= :numGuests
              AND r.available = true
              AND r.id NOT IN (
                  SELECT b.room.id FROM Booking b
                  WHERE b.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN')
                    AND b.dateRange.checkInDate  < :checkOut
                    AND b.dateRange.checkOutDate > :checkIn
              )
            """)
    List<Room> findAvailableRoomsForProperty(@Param("propertyId") Long propertyId,
                                              @Param("numGuests") int numGuests,
                                              @Param("checkIn") LocalDate checkIn,
                                              @Param("checkOut") LocalDate checkOut);
}
