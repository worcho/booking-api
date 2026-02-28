package io.github.dvirisha.booking_api.booking;

import io.github.dvirisha.booking_api.booking.dto.BookingResponse;
import io.github.dvirisha.booking_api.booking.dto.CreateBookingRequest;
import io.github.dvirisha.booking_api.booking.dto.UpdateBookingRequest;
import io.github.dvirisha.booking_api.common.error.ConflictException;
import io.github.dvirisha.booking_api.common.error.NotFoundException;
import io.github.dvirisha.booking_api.room.Room;
import io.github.dvirisha.booking_api.room.RoomRepository;
import jakarta.websocket.server.PathParam;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Component
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    public BookingResponse create(CreateBookingRequest request) {
        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new NotFoundException("Room not found."));

        boolean bookingExist = bookingRepository.isBookingExist(request.roomId(), request.startDate(), request.endDate());
        if (bookingExist) {
            throw new ConflictException("Room is not unavailable for that period.");
        }

        return convertToDto(bookingRepository.save(new Booking(room.getId(),
                request.startDate(),
                request.endDate(),
                BookingStatus.CREATED,
                Instant.now())));
    }

    public BookingResponse findById(Long id) {
        return convertToDto(bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found.")));
    }

    public List<BookingResponse> findAll() {
        return bookingRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<BookingResponse> findByRoomId(Long id, LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findByRoomId(id, startDate, endDate)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional
    public void cancelBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found."));

        if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
            throw new ConflictException("Booking already cancelled");
        }
        if (!booking.getStartDate().isAfter(LocalDate.now())) {
            throw new ConflictException("Booking already started");
        }
        booking.setStatus(BookingStatus.CANCELLED);
    }

    @Transactional
    public BookingResponse update(Long id, UpdateBookingRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found."));

        if (!request.startDate().isAfter(LocalDate.now())) {
            throw new ConflictException("Booking already started");
        }

        boolean bookingExist = bookingRepository.isBookingExist(booking.getRoomId(), request.startDate(), request.endDate());
        if (bookingExist) {
            throw new ConflictException("Room is not unavailable for that period.");
        }

        booking.setStartDate(request.startDate());
        booking.setEndDate(request.endDate());
        return convertToDto(booking);
    }

    private BookingResponse convertToDto(Booking entity) {
        return new BookingResponse(entity.getId(),
                entity.getRoomId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus(),
                entity.getCreatedAt());
    }
}
