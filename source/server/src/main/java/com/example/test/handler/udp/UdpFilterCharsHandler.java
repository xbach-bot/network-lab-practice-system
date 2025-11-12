package com.example.test.handler.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.test.config.HandlerInfo;
import com.example.test.domain.response.problem.ProblemResult;
import com.example.test.handler.ProblemHandler;

@Component
@HandlerInfo(qCode = "JXM8Dp3u")
public class UdpFilterCharsHandler implements ProblemHandler {

    @Override
    public ProblemResult processUdp(DatagramSocket serverSocket,
            DatagramPacket packet,
            String studentCode,
            String qCode) throws Exception {

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        String str1 = randomString();
        String str2 = randomString();

        String dataToSend = requestId + ";" + str1 + ";" + str2;

        byte[] resp = dataToSend.getBytes(StandardCharsets.UTF_8);

        DatagramPacket response = new DatagramPacket(
                resp,
                resp.length,
                packet.getAddress(),
                packet.getPort());
        serverSocket.send(response);

        int prevTimeout = 0;
        try {
            prevTimeout = serverSocket.getSoTimeout();
        } catch (Exception ignore) {
        }

        final int WAIT_MS = 100;
        serverSocket.setSoTimeout(WAIT_MS);

        String studentResult = null;
        long deadline = System.currentTimeMillis() + WAIT_MS;
        while (System.currentTimeMillis() < deadline) {
            byte[] buffer = new byte[2048];
            DatagramPacket clientPacket = new DatagramPacket(buffer, buffer.length);
            try {
                serverSocket.receive(clientPacket);

                if (!clientPacket.getAddress().equals(packet.getAddress())
                        || clientPacket.getPort() != packet.getPort()) {

                    continue;
                }

                String msg = new String(
                        clientPacket.getData(),
                        0,
                        clientPacket.getLength(),
                        StandardCharsets.UTF_8).trim();

                if (!msg.startsWith(requestId + ";")) {

                    continue;
                }

                studentResult = msg;
                break;
            } catch (SocketTimeoutException ste) {

                break;
            }
        }

        try {
            serverSocket.setSoTimeout(prevTimeout);
        } catch (Exception ignore) {
        }

        if (studentResult == null) {
            ProblemResult timeoutResult = new ProblemResult();
            timeoutResult.setInputData(dataToSend);
            timeoutResult.setStudentResult("Chưa gửi lên kết quả");
            timeoutResult.setExpectedResult(requestId + ";" + filter(str1, str2));
            timeoutResult.setCorrect(false);
            timeoutResult.setStatus("Chưa hoàn thành");
            return timeoutResult;
        }

        String filtered = filter(str1, str2);
        String expectedResult = requestId + ";" + filtered;

        boolean correct = expectedResult.equals(studentResult);

        ProblemResult result = new ProblemResult();
        result.setInputData(dataToSend);
        result.setStudentResult(studentResult);
        result.setExpectedResult(expectedResult);
        result.setCorrect(correct);
        result.setStatus(correct ? "Đúng" : "Sai");

        return result;
    }

    private String randomString() {
        String chars = "abcdefghijklmnopqrstuvwxyz123456789";
        Random r = new Random();
        int len = 10 + r.nextInt(5);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }

    private String filter(String s1, String s2) {
        Set<Character> remove = new HashSet<>();
        for (char c : s2.toCharArray())
            remove.add(c);

        StringBuilder out = new StringBuilder();
        for (char c : s1.toCharArray()) {
            if (!remove.contains(c))
                out.append(c);
        }
        return out.toString();
    }
}
