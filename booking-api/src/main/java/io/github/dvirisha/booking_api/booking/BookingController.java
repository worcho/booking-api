package io.github.dvirisha.booking_api.booking;

import io.github.dvirisha.booking_api.booking.dto.BookingResponse;
import io.github.dvirisha.booking_api.booking.dto.CreateBookingRequest;
import io.github.dvirisha.booking_api.booking.dto.UpdateBookingRequest;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@Valid @RequestBody CreateBookingRequest request) {
        return bookingService.create(request);
    }

    @GetMapping("/{id}")
    public BookingResponse findById(@PathVariable Long id) {
        return bookingService.findById(id);
    }

    @GetMapping("/room/{roomId}")
    public List<BookingResponse> findByRoomId(@PathVariable Long roomId, @RequestParam("from") LocalDate startDate, @RequestParam("to") LocalDate endDate) {
        return bookingService.findByRoomId(roomId, startDate, endDate);
    }

    @PostMapping("/{id}/cancel")
    public void cancelById(@PathVariable Long id) {
        bookingService.cancelBookingById(id);
    }

    @PatchMapping("/{id}")
    public BookingResponse update(@PathVariable Long id, @Valid @RequestBody UpdateBookingRequest request) {
        return bookingService.update(id, request);
    }
}
