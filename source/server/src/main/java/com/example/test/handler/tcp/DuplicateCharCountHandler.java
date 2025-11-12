package com.example.test.handler.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.example.test.config.HandlerInfo;
import com.example.test.domain.response.problem.ProblemResult;
import com.example.test.handler.ProblemHandler;

@Component
@HandlerInfo(qCode = "6TR9yXwW")
public class DuplicateCharCountHandler implements ProblemHandler {

    @Override
    public ProblemResult processTcpBuffered(Socket socket,
            String studentCode,
            String qCode) throws Exception {

        socket.setSoTimeout(5000);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()));

        ProblemResult result = new ProblemResult();
        String randomStr = null;
        String studentResponse = null;

        randomStr = randomString();

        writer.write(randomStr);
        writer.newLine();
        writer.flush();

        try {
            studentResponse = reader.readLine();
        } catch (SocketTimeoutException ste) {

            result.setInputData(randomStr);
            result.setStudentResult("Time Limit Exceeded");
            result.setExpectedResult(buildExpected(randomStr));
            result.setCorrect(false);
            result.setStatus("Sai");
            return result;
        }

        String expected = buildExpected(randomStr);

        boolean correct = expected.equals(studentResponse);

        result.setInputData(randomStr);
        result.setStudentResult(studentResponse);
        result.setExpectedResult(expected);
        result.setCorrect(correct);
        result.setStatus(correct ? "Đúng" : "Sai");

        return result;

    }

    private String randomString() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ";
        Random r = new Random();
        int len = 12 + r.nextInt(7);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String buildExpected(String s) {
        Map<Character, Integer> counts = new LinkedHashMap<>();
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                counts.put(c, counts.getOrDefault(c, 0) + 1);
            }
        }

        StringBuilder out = new StringBuilder();
        for (Map.Entry<Character, Integer> e : counts.entrySet()) {
            if (e.getValue() > 1) {
                out.append(e.getKey()).append(":").append(e.getValue()).append(",");
            }
        }

        return out.toString();
    }
}
