package com.example.test.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.example.test.service.JwtService;
import com.example.test.service.SocketService;
import org.springframework.security.oauth2.jwt.Jwt;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppGateway {

    private final SocketIOServer server;
    private final Map<String, SocketIOClient> clients = new ConcurrentHashMap<>();
    private final SocketService socketService;
    private final JwtService jwtService;

    public AppGateway(SocketIOServer server, SocketService socketService, JwtService jwtService) {
        this.server = server;
        this.socketService = socketService;
        this.jwtService = jwtService;
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        try {
            Jwt jwt = jwtService.checkAccessToken(client.getHandshakeData().getSingleUrlParam("accessToken"));
            if (jwt == null) {
                client.disconnect();
                return;
            }

            String usrEmail = jwt.getClaim("sub");
            if (usrEmail != null) {
                clients.put(usrEmail, client);
                log.info("Client connected: " + usrEmail);
            } else {
                client.disconnect();
            }
        } catch (Exception e) {
            client.disconnect();
            return;
        }

    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String userIdToRemove = null;
        for (Map.Entry<String, SocketIOClient> entry : clients.entrySet()) {
            if (entry.getValue().getSessionId().equals(client.getSessionId())) {
                userIdToRemove = entry.getKey();
                break;
            }
        }
        if (userIdToRemove != null) {
            clients.remove(userIdToRemove);
        }
        log.info("Client disconnected: " + userIdToRemove);
    }

    @OnEvent("message")
    public void onMessage(SocketIOClient client, Object payload) {
        server.getBroadcastOperations().sendEvent("message", payload);
    }

    @OnEvent("typing")
    public void onTyping(SocketIOClient client, Object payload) {
        String room = client.getNamespace().getName();
        this.socketService.sendEventExceptSender(room, "typing", client, payload);
    }

    @OnEvent("stopTyping")
    public void onStopTyping(SocketIOClient client, Object payload) {
        String room = client.getNamespace().getName();
        this.socketService.sendEventExceptSender(room, "stopTyping", client, payload);
    }

    @OnEvent("deleteMessage")
    public void onDeleteMessage(SocketIOClient client, Object payload) {
        server.getBroadcastOperations().sendEvent("deleteMessage", payload);
    }

}
