package com.example.test.socket.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.test.config.HandlerRegistry;
import com.example.test.domain.User;
import com.example.test.domain.response.problem.ProblemResult;
import com.example.test.handler.ProblemHandler;
import com.example.test.service.UserService;

import jakarta.annotation.PostConstruct;

@Component
public class UdpServer {

    private static final int PORT = 2209;

    @Autowired
    private HandlerRegistry handlerRegistry;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void start() {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(PORT)) {

                System.out.println("✅ UDP Server running at port " + PORT);

                byte[] buffer = new byte[1024];

                while (true) {
                    ProblemResult res;
                    String student = null;
                    String qcode = null;
                    try {
                        DatagramPacket clientPacket = new DatagramPacket(buffer, buffer.length);

                        socket.receive(clientPacket);
                        String header = new String(clientPacket.getData(), 0, clientPacket.getLength());
                        String[] parts = header.split(";");
                        student = parts[1];
                        qcode = parts[2];
                        User u = userService.findByStudentId(student);

                        if (u == null) {
                            return;
                        }
                        ProblemHandler handler = handlerRegistry.get(qcode);

                        res = handler.processUdp(socket, clientPacket, student, qcode);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
