package io.github.dvirisha.booking_api;

import io.github.dvirisha.booking_api.auth.AuthService;
import io.github.dvirisha.booking_api.booking.Booking;
import io.github.dvirisha.booking_api.booking.BookingRepository;
import io.github.dvirisha.booking_api.booking.BookingService;
import io.github.dvirisha.booking_api.booking.BookingStatus;
import io.github.dvirisha.booking_api.booking.dto.BookingResponse;
import io.github.dvirisha.booking_api.booking.dto.CreateBookingRequest;
import io.github.dvirisha.booking_api.common.error.ConflictException;
import io.github.dvirisha.booking_api.common.error.NotFoundException;
import io.github.dvirisha.booking_api.room.Room;
import io.github.dvirisha.booking_api.room.RoomRepository;
import io.github.dvirisha.booking_api.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private BookingService bookingService;

    @Nested
    @DisplayName("create()")
    class CreateTests {
        @Test
        void shouldCreateBookingSuccessfully() {
            CreateBookingRequest request = new CreateBookingRequest(
                    1L, LocalDate.of(2026, 4, 5), LocalDate.of(2026, 4, 10));

            Room room = new Room(
                    1L, "TestRoom", 10, new BigDecimal("50.00"), Instant.now());

            User user = new User();
            user.setId(1L);
            user.setUsername("TestUser");

            when(roomRepository.findById(1L))
                    .thenReturn(Optional.of(room));
            when(roomRepository.lockById(room.getId()))
                    .thenReturn(Optional.of(room));
            when(bookingRepository.isBookingExist(room.getId(), request.startDate(), request.endDate()))
                    .thenReturn(false);
            when(authService.getCurrentUser())
                    .thenReturn(user);
            when(bookingRepository.save(any(Booking.class)))
                    .thenAnswer(invocation -> {
                        Booking booking = invocation.getArgument(0);
                        booking.setId(1L);
                        return booking;
                    });

            BookingResponse response = bookingService.create(request);

            assertAll(
                    () -> assertEquals(1L, response.id()),
                    () -> assertEquals(LocalDate.of(2026, 4, 5), response.startDate()),
                    () -> assertEquals(LocalDate.of(2026, 4, 10), response.endDate()),
                    () -> assertEquals(1L, response.roomId()),
                    () -> assertEquals("TestRoom", response.roomName()),
                    () -> assertEquals(BookingStatus.CREATED, response.status()),
                    () -> assertEquals("TestUser", response.username()),
                    () -> assertNotNull(response.createdAt())
            );

            verify(roomRepository).lockById(1L);
        }

        @Test
        void shouldThrowNotFoundWhenRoomDoesNotExist() {
            CreateBookingRequest request = new CreateBookingRequest(
                    1L, LocalDate.of(2026, 4, 5), LocalDate.of(2026, 4, 10));

            when(roomRepository.findById(1L))
                    .thenReturn(Optional.empty());

            assertThrowsExactly(NotFoundException.class,
                    () -> bookingService.create(request));

            verifyNoInteractions(bookingRepository);
        }

        @Test
        void shouldThrowNotFoundWhenRoomLockFails() {
            CreateBookingRequest request = new CreateBookingRequest(
                    1L, LocalDate.of(2026, 4, 5), LocalDate.of(2026, 4, 10));

            Room room = new Room(
                    1L, "TestRoom", 10, new BigDecimal("50.00"), Instant.now());

            when(roomRepository.findById(1L))
                    .thenReturn(Optional.of(room));
            when(roomRepository.lockById(1L))
                    .thenReturn(Optional.empty());

            assertThrowsExactly(NotFoundException.class,
                    () -> bookingService.create(request));

            verifyNoInteractions(bookingRepository);
        }

        @Test
        void shouldThrowConflictWhenRoomAlreadyBooked() {
            CreateBookingRequest request = new CreateBookingRequest(
                    1L, LocalDate.of(2026, 4, 5), LocalDate.of(2026, 4, 10));

            Room room = new Room(
                    1L, "TestRoom", 10, new BigDecimal("50.00"), Instant.now());

            when(roomRepository.findById(1L))
                    .thenReturn(Optional.of(room));
            when(roomRepository.lockById(room.getId()))
                    .thenReturn(Optional.of(room));
            when(bookingRepository.isBookingExist(room.getId(), request.startDate(), request.endDate()))
                    .thenReturn(true);

            assertThrowsExactly(ConflictException.class,
                    () -> bookingService.create(request));

            verify(bookingRepository, never()).save(any());
        }
    }

}
