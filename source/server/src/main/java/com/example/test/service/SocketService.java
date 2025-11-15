package com.example.test.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.corundumstudio.socketio.SocketIOClient;
import com.example.test.core.error.BadRequestException;
import com.example.test.domain.Room;

@Service
public class SocketService {

    private final RoomService roomService;
    public SocketService(RoomService roomService) {
        this.roomService = roomService;
    }

    public void sendEventExceptSender(String room, String eventName, SocketIOClient senderClient,
            Object payload) {
        for (SocketIOClient client : senderClient.getNamespace().getRoomOperations(room).getClients()) {
            if (!client.getSessionId().equals(senderClient.getSessionId())) {
                client.sendEvent(eventName, payload);
            }
        }
    }

    public void sendEventToClient(SocketIOClient client, String eventName, Object payload) {
        client.sendEvent(eventName, payload);
    }

    public void userJoinRoom(SocketIOClient client, String usrEmail) throws BadRequestException {
        List<Room> rooms = roomService.getRoomsByUsers(usrEmail);
        client.joinRoom("public");
        if (rooms.size() == 0) {
            return;
        }
        for (Room room : rooms) {
            client.joinRoom("room_" + room.getId());
        }

    }

}
