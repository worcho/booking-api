package io.github.dvirisha.booking_api.room;

import io.github.dvirisha.booking_api.room.dto.CreateRoomRequest;
import io.github.dvirisha.booking_api.room.dto.RoomResponse;
import io.github.dvirisha.booking_api.common.error.ConflictException;
import io.github.dvirisha.booking_api.common.error.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoomService {

    private final RoomRepository repository;

    public RoomService(RoomRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public RoomResponse create(CreateRoomRequest request) {
        if (repository.existsByNameIgnoreCase(request.name())) {
            throw new ConflictException("Room with name '%s' already exists.".formatted(request.name()));
        }
        return convertToDto(repository.save(new Room(request.name(), request.capacity())));
    }

    public RoomResponse findById(Long id) {
        return convertToDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found.")));
    }

    public List<RoomResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomResponse updateById(Long id, CreateRoomRequest request) {
        Room room = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found."));
        updateEntity(room, request);
        return convertToDto(room);
    }

    @Transactional
    public void deleteById(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new NotFoundException("Room not found.");
        }
    }

    private RoomResponse convertToDto(Room entity) {
        return new RoomResponse(entity.getId(),
                entity.getName(),
                entity.getCapacity(),
                entity.getCreatedAt());
    }

    private void updateEntity(Room entity, CreateRoomRequest roomRequest) {
        entity.setName(roomRequest.name());
        entity.setCapacity(roomRequest.capacity());
    }
}
