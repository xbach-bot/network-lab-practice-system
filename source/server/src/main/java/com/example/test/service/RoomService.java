package com.example.test.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.test.domain.Room;
import com.example.test.repository.RoomRepository;
import com.example.test.domain.User;
import com.example.test.repository.UserRepository;
import com.example.test.core.error.BadRequestException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    private final UserRepository userRepository;

    public RoomService(RoomRepository roomRepository, UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    public Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    public List<Room> getMyRooms() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);

        return user.getRooms();
    }

    @Transactional
    public Room getOrCreatePrivateRoom(Long userId1, Long userId2) throws BadRequestException {
        if (userId1 == null || userId2 == null) {
            throw new BadRequestException("User ids required");
        }

        if (userId1.equals(userId2)) {
            throw new BadRequestException("Cannot create private room with self");
        }

        User u1 = userRepository.findById(userId1).orElse(null);
        User u2 = userRepository.findById(userId2).orElse(null);

        if (u1 == null || u2 == null) {
            throw new BadRequestException("User not found");
        }

        Room existing = roomRepository.findPrivateRoomBetween(userId1, userId2);
        if (existing == null) {
            existing = roomRepository.findPrivateRoomBetween(userId2, userId1);
        }

        if (existing != null) {
            return existing;
        }

        Room room = new Room();
        room.setName("private_" + Math.min(userId1, userId2) + "_" + Math.max(userId1, userId2));

        List<User> participants = new ArrayList<>();
        participants.add(u1);
        participants.add(u2);
        room.setParticipants(participants);

        Room res = roomRepository.save(room);

        u1.getRooms().add(res);
        u2.getRooms().add(res);
        userRepository.save(u1);
        userRepository.save(u2);
        return res;
    }

    public List<Room> getRoomsByUsers(String userEmail) throws BadRequestException {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new BadRequestException("User not found");
        }

        return roomRepository.findAll((root, query, cb) -> cb.isMember(user, root.get("participants")));
    }

}
