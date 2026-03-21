package io.github.dvirisha.booking_api.booking;

import io.github.dvirisha.booking_api.common.PageResponse;
import io.github.dvirisha.booking_api.booking.dto.BookingResponse;
import io.github.dvirisha.booking_api.booking.dto.CreateBookingRequest;
import io.github.dvirisha.booking_api.booking.dto.GetBookingFilter;
import io.github.dvirisha.booking_api.booking.dto.UpdateBookingRequest;
import io.github.dvirisha.booking_api.common.error.ConflictException;
import io.github.dvirisha.booking_api.common.error.NotFoundException;
import io.github.dvirisha.booking_api.user.User;
import io.github.dvirisha.booking_api.auth.AuthService;
import io.github.dvirisha.booking_api.common.util.PageUtil;
import io.github.dvirisha.booking_api.room.Room;
import io.github.dvirisha.booking_api.room.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Component
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final AuthService authService;

    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository, AuthService authService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.authService = authService;
    }

    @Transactional
    public BookingResponse create(CreateBookingRequest request) {
        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new NotFoundException("Room not found."));

        roomRepository.lockById(room.getId())
                .orElseThrow(() -> new NotFoundException("Room not found."));

        if (bookingRepository.isBookingExist(request.roomId(), request.startDate(), request.endDate())) {
            throw new ConflictException("Room is not unavailable for that period.");
        }

        User currentUser;
        try {
            currentUser = authService.getCurrentUser();
        } catch (UserPrincipalNotFoundException e) {
            throw new UsernameNotFoundException("You are not logged in");
        }

        return convertToDto(bookingRepository.save(new Booking(room,
                request.startDate(),
                request.endDate(),
                BookingStatus.CREATED,
                currentUser,
                Instant.now())));
    }

    public BookingResponse findById(Long id) {
        return convertToDto(bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found.")));
    }

    public PageResponse<BookingResponse> findAll(GetBookingFilter filter, Pageable pageable) {
        if (filter.startDate() != null && filter.endDate() != null && !filter.startDate().isBefore(filter.endDate())) {
            throw new IllegalArgumentException("startDate must be before endDate");
        }
        Specification<Booking> specification = Specification.where(
                BookingSpecifications.withRoomId(filter.roomId())
                        .and(BookingSpecifications.withStatus(filter.status()))
                        .and(BookingSpecifications.withStartDate(filter.startDate()))
                        .and(BookingSpecifications.withEndDate(filter.endDate()))
        );

        Page<BookingResponse> page = bookingRepository.findAll(specification, PageUtil.normalizePageable(pageable, Set.of("id", "startDate", "endDate", "status")))
                .map(this::convertToDto);

        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                PageUtil.toSortList(page.getSort())
        );
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

        boolean bookingExist = bookingRepository.isBookingExist(booking.getRoom().getId(), request.startDate(), request.endDate());
        if (bookingExist) {
            throw new ConflictException("Room is not unavailable for that period.");
        }

        booking.setStartDate(request.startDate());
        booking.setEndDate(request.endDate());
        return convertToDto(booking);
    }

    private BookingResponse convertToDto(Booking entity) {
        return new BookingResponse(entity.getId(),
                entity.getRoom().getId(),
                entity.getRoom().getName(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus(),
                entity.getUser().getUsername(),
                entity.getCreatedAt());
    }
}
