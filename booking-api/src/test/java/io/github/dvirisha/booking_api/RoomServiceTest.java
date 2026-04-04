package io.github.dvirisha.booking_api;

import io.github.dvirisha.booking_api.common.error.ConflictException;
import io.github.dvirisha.booking_api.common.error.NotFoundException;
import io.github.dvirisha.booking_api.room.Room;
import io.github.dvirisha.booking_api.room.RoomRepository;
import io.github.dvirisha.booking_api.room.RoomService;
import io.github.dvirisha.booking_api.room.dto.CreateRoomRequest;
import io.github.dvirisha.booking_api.room.dto.GetRoomFilter;
import io.github.dvirisha.booking_api.room.dto.RoomResponse;
import io.github.dvirisha.booking_api.room.dto.UpdateRoomRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    void shouldCreateRoomSuccessfully() {
        CreateRoomRequest request = new CreateRoomRequest("TestRoom", 10, new BigDecimal("50.00"));

        when(roomRepository.existsByNameIgnoreCase(request.name()))
                .thenReturn(false);
        when(roomRepository.save(any(Room.class)))
                .thenAnswer(invocation -> {
                    Room room = invocation.getArgument(0);
                    room.setId(1L);
                    return room;
                });

        RoomResponse response = roomService.create(request);

        assertAll(
                () -> assertEquals(1L, response.id()),
                () -> assertEquals("TestRoom", response.name()),
                () -> assertEquals(10, response.capacity()),
                () -> assertEquals(new BigDecimal("50.00"), response.price()),
                () -> assertNotNull(response.createdAt())
        );

        verify(roomRepository).existsByNameIgnoreCase("TestRoom");

        ArgumentCaptor<Room> captor = ArgumentCaptor.forClass(Room.class);
        verify(roomRepository).save(captor.capture());

        Room savedRoom = captor.getValue();
        assertEquals("TestRoom", savedRoom.getName());
        assertEquals(10, savedRoom.getCapacity());
        assertEquals(new BigDecimal("50.00"), savedRoom.getPrice());
    }

    @Test
    void shouldNotCreateRoomSuccessfully() {
        CreateRoomRequest request = new CreateRoomRequest("TestRoom", 10, new BigDecimal("50.00"));

        when(roomRepository.existsByNameIgnoreCase(request.name()))
                .thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> roomService.create(request));

        assertEquals("Room with name '%s' already exists.".formatted(request.name()),
                exception.getMessage());

        verify(roomRepository, never()).save(any());
    }

    @Test
    void shouldReturnRoomWhenFound() {
        Room room = new Room(1L, "TestRoom", 10, new BigDecimal("50.00"), Instant.now());

        when(roomRepository.findById(anyLong()))
                .thenReturn(Optional.of(room));

        RoomResponse response = roomService.findById(anyLong());

        assertAll(
                () -> assertEquals(1L, response.id()),
                () -> assertEquals("TestRoom", response.name()),
                () -> assertEquals(10, response.capacity()),
                () -> assertEquals(new BigDecimal("50.00"), response.price()),
                () -> assertNotNull(response.createdAt())
        );

        verify(roomRepository).findById(anyLong());
    }

    @Test
    void shouldThrowNotFoundWhenRoomDoesNotExist() {
        when(roomRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrowsExactly(NotFoundException.class,
                () -> roomService.findById(1L));

        assertEquals("Room not found.", exception.getMessage());
    }

    @Test
    void shouldUpdateRoomSuccessfully() {
        Room room = new Room(1L, "TestRoom", 10, new BigDecimal("50.00"), Instant.now());
        UpdateRoomRequest request = new UpdateRoomRequest("TestRoomUpdated", 5, new BigDecimal("50.00"));

        when(roomRepository.findById(anyLong()))
                .thenReturn(Optional.of(room));

        RoomResponse roomResponse = roomService.updateById(1L, request);

        assertAll(
                () -> assertEquals("TestRoomUpdated", room.getName()),
                () -> assertEquals(5, room.getCapacity()),
                () -> assertEquals(new BigDecimal("50.00"), room.getPrice())
        );

        assertAll(
                () -> assertEquals(1L, roomResponse.id()),
                () -> assertEquals("TestRoomUpdated", roomResponse.name()),
                () -> assertEquals(5, roomResponse.capacity()),
                () -> assertEquals(new BigDecimal("50.00"), roomResponse.price())
        );
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingNonExistentRoom() {
        UpdateRoomRequest request = new UpdateRoomRequest("TestRoomUpdated", 5, new BigDecimal("50.00"));

        when(roomRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrowsExactly(NotFoundException.class,
                () -> roomService.updateById(1L, request));
    }

    @Test
    void shouldDeleteRoomSuccessfully() {
        when(roomRepository.existsById(1L))
                .thenReturn(true);

        roomService.deleteById(1L);

        verify(roomRepository).deleteById(1L);
    }

    @Test
    void shouldThrowNotFoundWhenDeletingNonExistentRoom() {
        when(roomRepository.existsById(1L))
                .thenReturn(false);

        assertThrowsExactly(NotFoundException.class,
                () -> roomService.deleteById(1L));

        verify(roomRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldThrowWhenPriceMinGreaterThanPriceMax() {
        GetRoomFilter filter = new GetRoomFilter(5, new BigDecimal("10.00"), new BigDecimal("5"));

        assertThrowsExactly(IllegalArgumentException.class,
                () -> roomService.findAll(filter, null));

        verifyNoInteractions(roomRepository);
    }
}
