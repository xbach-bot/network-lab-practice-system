package com.example.test.controller;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.core.error.BadRequestException;
import com.example.test.domain.Room;
import com.example.test.domain.User;
import com.example.test.domain.request.room.PrivateRoomDTO;
import com.example.test.domain.response.room.ResponseRoomDTO;
import com.example.test.repository.UserRepository;
import com.example.test.service.RoomService;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final UserRepository userRepository;

    public RoomController(RoomService roomService, UserRepository userRepository) {
        this.roomService = roomService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<List<ResponseRoomDTO>> getMyRooms() {
        List<ResponseRoomDTO> rooms = this.roomService.getMyRooms();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @PostMapping("/private")
    public ResponseEntity<Room> getOrCreatePrivate(@RequestBody PrivateRoomDTO req) throws BadRequestException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User me = userRepository.findByEmail(email);
        if (me == null) {
            throw new BadRequestException("User not found");
        }

        Room room = roomService.getOrCreatePrivateRoom(me.getId(), req.getTargetUserId());
        return new ResponseEntity<>(room, HttpStatus.OK);
    }



}
