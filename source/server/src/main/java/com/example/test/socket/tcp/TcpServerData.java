package com.example.test.socket.tcp;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.test.config.HandlerRegistry;
import com.example.test.domain.User;
import com.example.test.domain.response.problem.ProblemResult;
import com.example.test.handler.ProblemHandler;
import com.example.test.service.UserService;

import jakarta.annotation.PostConstruct;

@Component
public class TcpServerData {

    @Autowired
    private HandlerRegistry handlerRegistry;

    @Autowired
    private UserService userService;

    private final int PORT = 2207;

    @PostConstruct
    public void start() {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(PORT)) {
                System.out.println("TCP DATA server running at " + PORT);

                while (true) {
                    Socket socket = server.accept();
                    new Thread(() -> handle(socket)).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handle(Socket socket) {
        ProblemResult res;
        String student = null;
        String qcode = null;
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            String header = dis.readUTF(); // "sv;qcode"
            String[] p = header.split(";");

            student = p[0];
            qcode = p[1];

            User u = userService.findByStudentId(student);

            if (u == null) {
                return;
            }

            ProblemHandler handler = handlerRegistry.get(qcode);

            res = handler.processTcpData(socket, student, qcode);

            socket.close();

        } catch (Exception e) {
            res = new ProblemResult();

            res.setStudentResult("ERROR: " + e.toString());
            res.setExpectedResult(null);
            res.setCorrect(false);
            res.setStatus("Chưa hoàn thành");
            e.printStackTrace();
        }
    }
}
