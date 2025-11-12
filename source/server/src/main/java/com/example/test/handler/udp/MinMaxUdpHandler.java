package com.example.test.handler.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.test.config.HandlerInfo;
import com.example.test.domain.response.problem.ProblemResult;
import com.example.test.handler.ProblemHandler;

@Component
@HandlerInfo(qCode = "azD96nTm")
public class MinMaxUdpHandler implements ProblemHandler {

    @Override
    public ProblemResult processUdp(DatagramSocket serverSocket,
            DatagramPacket packet,
            String studentCode,
            String qCode) throws Exception {

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        int[] arr = new int[50];
        Random r = new Random();
        for (int i = 0; i < 50; i++) {
            arr[i] = r.nextInt(1000);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(requestId).append(";");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1)
                sb.append(",");
        }
        String dataToSend = sb.toString();

        byte[] resp = dataToSend.getBytes();
        DatagramPacket response = new DatagramPacket(resp, resp.length, packet.getAddress(), packet.getPort());
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

                String msg = new String(clientPacket.getData(), 0, clientPacket.getLength()).trim();

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

        // Tính đáp án chuẩn
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (int v : arr) {
            if (v > max)
                max = v;
            if (v < min)
                min = v;
        }
        String expectedResult = requestId + ";" + max + "," + min;

        if (studentResult == null) {
            ProblemResult timeoutResult = new ProblemResult();
            timeoutResult.setInputData(dataToSend);
            timeoutResult.setStudentResult("Chưa gửi lên kết quả");
            timeoutResult.setExpectedResult(expectedResult);
            timeoutResult.setCorrect(false);
            timeoutResult.setStatus("Chưa hoàn thành");
            return timeoutResult;
        }

        boolean correct = expectedResult.equals(studentResult);

        ProblemResult result = new ProblemResult();
        result.setInputData(dataToSend);
        result.setStudentResult(studentResult);
        result.setExpectedResult(expectedResult);
        result.setCorrect(correct);
        result.setStatus(correct ? "Đúng" : "Sai");

        return result;
    }
}
