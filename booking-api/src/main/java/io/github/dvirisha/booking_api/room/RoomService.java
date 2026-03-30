package io.github.dvirisha.booking_api.room;

import io.github.dvirisha.booking_api.common.PageResponse;
import io.github.dvirisha.booking_api.common.error.ConflictException;
import io.github.dvirisha.booking_api.common.error.NotFoundException;
import io.github.dvirisha.booking_api.common.util.PageUtil;
import io.github.dvirisha.booking_api.room.dto.CreateRoomRequest;
import io.github.dvirisha.booking_api.room.dto.GetRoomFilter;
import io.github.dvirisha.booking_api.room.dto.RoomResponse;
import io.github.dvirisha.booking_api.room.dto.UpdateRoomRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class RoomService {

    private final int PAGE_MAX_SIZE = 100;
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public RoomResponse create(CreateRoomRequest request) {
        if (roomRepository.existsByNameIgnoreCase(request.name())) {
            throw new ConflictException("Room with name '%s' already exists.".formatted(request.name()));
        }
        return convertToDto(roomRepository.save(new Room(request.name(), request.capacity(), request.price())));
    }

    public RoomResponse findById(Long id) {
        return convertToDto(roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found.")));
    }

    @PreAuthorize("hasRole('USER')")
    public PageResponse<RoomResponse> findAll(GetRoomFilter filter, Pageable pageable) {
        if (filter.priceMin() != null && filter.priceMax() != null && filter.priceMin().compareTo(filter.priceMax()) > 0) {
            throw new IllegalArgumentException("priceMin cannot be greater than priceMax");
        }

        Specification<Room> specification =
                Specification.where(RoomSpecifications.capacityAtLeast(filter.capacityMin())
                                .and(RoomSpecifications.priceAtLeast(filter.priceMin())
                                .and(RoomSpecifications.priceAtMost(filter.priceMax()))));

        Page<RoomResponse> page = roomRepository.findAll(specification, PageUtil.normalizePageable(pageable, Set.of("id", "capacity", "price")))
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
    @PreAuthorize("hasRole('ADMIN')")
    public RoomResponse updateById(Long id, UpdateRoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found."));
        updateEntity(room, request);
        return convertToDto(room);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id) {
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
        } else {
            throw new NotFoundException("Room not found.");
        }
    }

    private RoomResponse convertToDto(Room entity) {
        return new RoomResponse(entity.getId(),
                entity.getName(),
                entity.getCapacity(),
                entity.getPrice(),
                entity.getCreatedAt());
    }

    private void updateEntity(Room entity, UpdateRoomRequest roomRequest) {
        entity.setName(roomRequest.name());
        entity.setCapacity(roomRequest.capacity());
        entity.setPrice(roomRequest.price());
    }
}
