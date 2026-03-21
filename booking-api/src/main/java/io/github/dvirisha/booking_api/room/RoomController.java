package io.github.dvirisha.booking_api.room;

import io.github.dvirisha.booking_api.common.PageResponse;
import io.github.dvirisha.booking_api.room.dto.CreateRoomRequest;
import io.github.dvirisha.booking_api.room.dto.GetRoomFilter;
import io.github.dvirisha.booking_api.room.dto.RoomResponse;
import io.github.dvirisha.booking_api.room.dto.UpdateRoomRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse create(@Valid @RequestBody CreateRoomRequest request) {
        return roomService.create(request);
    }

    @GetMapping
    public PageResponse<RoomResponse> getAll(@ModelAttribute GetRoomFilter filter,
                                             @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return roomService.findAll(filter, pageable);
    }

    @GetMapping("/{id}")
    public RoomResponse getById(@PathVariable Long id) {
        return roomService.findById(id);
    }

    @PutMapping("/{id}")
    public RoomResponse updateById(@PathVariable Long id, @Valid @RequestBody UpdateRoomRequest request) {
        return roomService.updateById(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        roomService.deleteById(id);
    }
}
