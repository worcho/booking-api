package io.github.dvirisha.booking_api;

import io.github.dvirisha.booking_api.auth.AuthService;
import io.github.dvirisha.booking_api.booking.Booking;
import io.github.dvirisha.booking_api.booking.BookingRepository;
import io.github.dvirisha.booking_api.booking.BookingService;
import io.github.dvirisha.booking_api.booking.BookingStatus;
import io.github.dvirisha.booking_api.booking.dto.BookingResponse;
import io.github.dvirisha.booking_api.booking.dto.CreateBookingRequest;
import io.github.dvirisha.booking_api.common.error.ConflictException;
import io.github.dvirisha.booking_api.common.error.ForbiddenException;
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
            CreateBookingRequest request = buildCreateBookingRequest();
            Room room = buildRoom();
            User user = buildUser();

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
                    () -> assertEquals(LocalDate.now().plusDays(1), response.startDate()),
                    () -> assertEquals(LocalDate.now().plusDays(5), response.endDate()),
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
            CreateBookingRequest request = buildCreateBookingRequest();

            when(roomRepository.findById(1L))
                    .thenReturn(Optional.empty());

            assertThrowsExactly(NotFoundException.class,
                    () -> bookingService.create(request));

            verifyNoInteractions(bookingRepository);
        }

        @Test
        void shouldThrowNotFoundWhenRoomLockFails() {
            CreateBookingRequest request = buildCreateBookingRequest();
            Room room = buildRoom();

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
            CreateBookingRequest request = buildCreateBookingRequest();
            Room room = buildRoom();

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

    @Nested
    @DisplayName("findById())")
    class findByIdTests {

        @Test
        void shouldReturnBookingWhenFound() {
            Room room = buildRoom();
            User user = buildUser();
            Booking booking = buildBooking(room, user);

            when(bookingRepository.findById(1L))
                    .thenReturn(Optional.of(booking));
            when(authService.getCurrentUserId())
                    .thenReturn(1L);

            BookingResponse response = bookingService.findById(1L);

            assertAll(
                    () -> assertEquals(1L, response.id()),
                    () -> assertEquals(LocalDate.now().plusDays(1), response.startDate()),
                    () -> assertEquals(LocalDate.now().plusDays(5), response.endDate()),
                    () -> assertEquals(1L, response.roomId()),
                    () -> assertEquals("TestRoom", response.roomName()),
                    () -> assertEquals(BookingStatus.CREATED, response.status()),
                    () -> assertEquals("TestUser", response.username()),
                    () -> assertNotNull(response.createdAt())
            );
        }

        @Test
        void shouldThrowNotFoundWhenBookingDoesNotExist() {
            when(bookingRepository.findById(1L))
                    .thenReturn(Optional.empty());

            assertThrowsExactly(NotFoundException.class,
                    () -> bookingService.findById(1L));

            verifyNoInteractions(authService);
        }

        @Test
        void shouldThrowForbiddenWhenUserDoNotOwnBooking() {
            User user = buildUser();

            Booking booking = buildBooking(null, user);

            when(bookingRepository.findById(1L))
                    .thenReturn(Optional.of(booking));
            when(authService.getCurrentUserId())
                    .thenReturn(2L);

            assertThrowsExactly(ForbiddenException.class,
                    () -> bookingService.findById(1L));
        }
    }

    @Nested
    @DisplayName("cancel())")
    class cancelBookingTests {

        @Test
        void shouldCancelBookingSuccessfully() {
            Booking booking = buildBooking(null, buildUser());

            when(bookingRepository.findById(1L))
                    .thenReturn(Optional.of(booking));
            when(authService.getCurrentUserId())
                    .thenReturn(1L);

            bookingService.cancelBookingById(1L);

            assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        }
    }

    private Room buildRoom() {
        Room room = new Room();
        room.setId(1L);
        room.setName("TestRoom");
        return room;
    }

    private User buildUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("TestUser");
        return user;
    }

    private Booking buildBooking(Room room, User user) {

        return new Booking(1L,
                room,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                BookingStatus.CREATED,
                user,
                Instant.now());
    }

    private CreateBookingRequest buildCreateBookingRequest() {
        return new CreateBookingRequest(
                1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));
    }
}
