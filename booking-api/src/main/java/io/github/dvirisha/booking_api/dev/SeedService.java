package io.github.dvirisha.booking_api.dev;

import io.github.dvirisha.booking_api.booking.Booking;
import io.github.dvirisha.booking_api.booking.BookingRepository;
import io.github.dvirisha.booking_api.booking.BookingStatus;
import io.github.dvirisha.booking_api.room.Room;
import io.github.dvirisha.booking_api.room.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class SeedService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public void seed(int roomCount, int bookingsPerRoom) {
        bookingRepository.deleteAllInBatch();
        roomRepository.deleteAllInBatch();

        List<Room> rooms = createRooms(roomCount);
        createBookingsPerRoom(rooms, bookingsPerRoom);
    }

    private List<Room> createRooms(int roomCount) {
        List<Room> rooms = new ArrayList<>();

        for (int i = 1; i <= roomCount; i++) {
            Room room = new Room();
            room.setName("Room " + i);
            room.setCapacity(ThreadLocalRandom.current().nextInt(1, 6));
            room.setPrice(BigDecimal.valueOf(
                    ThreadLocalRandom.current().nextInt(50, 301)
            ));
            room.setCreatedAt(Instant.now());

            rooms.add(room);
        }

        return roomRepository.saveAll(rooms);
    }

    private void createBookingsPerRoom(List<Room> rooms, int bookingsPerRoom) {
        List<Booking> allBookings = new ArrayList<>();

        for (Room room : rooms) {
            LocalDate cursor = LocalDate.now();

            for (int i = 0; i < bookingsPerRoom; i++) {
                int gapDays = ThreadLocalRandom.current().nextInt(0, 4);
                int stayDays = ThreadLocalRandom.current().nextInt(1, 8);

                LocalDate startDate = cursor.plusDays(gapDays);
                LocalDate endDate = startDate.plusDays(stayDays);

                Booking booking = new Booking();
                booking.setRoom(room);
                booking.setStartDate(startDate);
                booking.setEndDate(endDate);
                booking.setStatus(randomStatus());
                booking.setCreatedAt(Instant.now());

                allBookings.add(booking);

                cursor = endDate.plusDays(ThreadLocalRandom.current().nextInt(0, 3));
            }
        }

        bookingRepository.saveAll(allBookings);
    }

    private BookingStatus randomStatus() {
        BookingStatus[] statuses = {
                BookingStatus.PENDING,
                BookingStatus.CONFIRMED,
                BookingStatus.CANCELLED
        };

        return statuses[ThreadLocalRandom.current().nextInt(statuses.length)];
    }
}
