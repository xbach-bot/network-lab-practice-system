package com.example.test.handler.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.test.config.HandlerInfo;
import com.example.test.domain.response.problem.ProblemResult;
import com.example.test.handler.ProblemHandler;

@Component
@HandlerInfo(qCode = "iv00Hrq6")
public class SlidingWindowMaxHandler implements ProblemHandler {

    @Override
    public ProblemResult processUdp(DatagramSocket serverSocket,
            DatagramPacket packet,
            String studentCode,
            String qCode) throws Exception {

        Random r = new Random();
        int n = 6 + r.nextInt(5);
        int k = 2 + r.nextInt(n - 2);

        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = r.nextInt(20) + 1;
        }

        String requestId = UUID.randomUUID().toString().substring(0, 8);

        String serverMessage = requestId + ";" + n + ";" + k + ";" + join(arr);

        byte[] respBytes = serverMessage.getBytes(StandardCharsets.UTF_8);

        DatagramPacket response = new DatagramPacket(
                respBytes,
                respBytes.length,
                packet.getAddress(),
                packet.getPort());
        serverSocket.send(response);

        int prevTimeout = 0;
        try {
            prevTimeout = serverSocket.getSoTimeout();
        } catch (Exception ignore) {
        }
        final int WAIT_MS = 2000;
        serverSocket.setSoTimeout(WAIT_MS);

        String studentResult = null;
        long deadline = System.currentTimeMillis() + WAIT_MS;
        while (System.currentTimeMillis() < deadline) {
            byte[] buffer = new byte[1024];
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
            } catch (SocketTimeoutException toe) {

                break;
            }
        }

        try {
            serverSocket.setSoTimeout(prevTimeout);
        } catch (Exception ignore) {
        }

        if (studentResult == null) {
            ProblemResult timeoutResult = new ProblemResult();
            timeoutResult.setInputData(serverMessage);
            timeoutResult.setStudentResult("Chưa gửi lên kết quả");
            timeoutResult.setExpectedResult(requestId + ";" + join(maxSlidingWindow(arr, k)));
            timeoutResult.setCorrect(false);
            timeoutResult.setStatus("Chưa hoàn thành");
            return timeoutResult;
        }

        // Tính đáp án chuẩn
        int[] expectedArray = maxSlidingWindow(arr, k);
        String expectedResult = requestId + ";" + join(expectedArray);

        boolean correct = expectedResult.equals(studentResult);

        ProblemResult result = new ProblemResult();
        result.setInputData(serverMessage);
        result.setStudentResult(studentResult);
        result.setExpectedResult(expectedResult);
        result.setCorrect(correct);
        result.setStatus(correct ? "Đúng" : "Sai");

        return result;
    }

    private int[] maxSlidingWindow(int[] nums, int k) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i + k <= nums.length; i++) {
            int max = nums[i];
            for (int j = i; j < i + k; j++)
                max = Math.max(max, nums[j]);
            result.add(max);
        }
        return result.stream().mapToInt(i -> i).toArray();
    }

    private String join(int[] arr) {
        return Arrays.stream(arr)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));
    }
}
