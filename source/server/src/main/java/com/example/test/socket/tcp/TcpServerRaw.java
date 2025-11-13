package com.example.test.socket.tcp;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.test.config.HandlerRegistry;
import com.example.test.domain.User;
import com.example.test.domain.response.problem.ProblemResult;
import com.example.test.handler.ProblemHandler;
import com.example.test.service.SubmissionService;
import com.example.test.service.UserService;

import jakarta.annotation.PostConstruct;

@Component
public class TcpServerRaw {

    @Autowired
    private HandlerRegistry handlerRegistry;
    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UserService userService;

    private final int PORT = 2208;

    @PostConstruct
    public void start() {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(PORT)) {
                System.out.println("TCP RAW server running at " + PORT);

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
            InputStream in = socket.getInputStream();

            byte[] buffer = new byte[1024];
            int len = in.read(buffer);

            String header = new String(buffer, 0, len);

            String[] p = header.split(";");
            student = p[0];
            qcode = p[1];

            User u = userService.findByStudentId(student);

            if (u == null) {
                return;
            }

            ProblemHandler handler = handlerRegistry.get(qcode);

            res = handler.processTcpRaw(socket, student, qcode);

            submissionService.save(student, qcode, res);
            socket.close();

        } catch (Exception e) {
            res = new ProblemResult();

            res.setStudentResult("ERROR: " + e.toString());
            res.setExpectedResult(null);
            res.setCorrect(false);
            res.setStatus("Chưa hoàn thành");
            submissionService.save(student, qcode, res);
            e.printStackTrace();
        }
    }
}
