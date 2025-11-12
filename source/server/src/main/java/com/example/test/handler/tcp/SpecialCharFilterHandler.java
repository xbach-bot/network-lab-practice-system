package com.example.test.handler.tcp;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.example.test.handler.ProblemHandler;
import com.example.test.config.HandlerInfo;
import com.example.test.domain.response.problem.ProblemResult;

import java.io.*;
import java.net.Socket;

@Component
@HandlerInfo(qCode = "xVX7k3lq")
public class SpecialCharFilterHandler implements ProblemHandler {

    @Override
    public ProblemResult processTcpBuffered(Socket socket,
            String studentCode,
            String qCode) throws Exception {

        socket.setSoTimeout(5000);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        String randomStr = generateRandom();
        writer.write(randomStr);
        writer.newLine();
        writer.flush();
        String studentResult;
        try {
            studentResult = reader.readLine();
        } catch (Exception e) {

            ProblemResult result = new ProblemResult();
            result.setInputData(randomStr);
            result.setStudentResult("TimeLimitExceeded");
            result.setExpectedResult(removeSpecialAndDuplicates(randomStr));
            result.setCorrect(false);
            result.setStatus("Sai");
            return result;
        }

        String expected = removeSpecialAndDuplicates(randomStr);

        boolean correct = expected.equals(studentResult);

        ProblemResult result = new ProblemResult();
        result.setInputData(randomStr);
        result.setStudentResult(studentResult);
        result.setExpectedResult(expected);
        result.setCorrect(correct);
        result.setStatus(correct ? "Đúng" : "Sai");

        return result;
    }

    private String generateRandom() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+=-";
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        int len = 20 + r.nextInt(20);

        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }

        return sb.toString();
    }

    private String removeSpecialAndDuplicates(String s) {
        Set<Character> seen = new HashSet<>();
        StringBuilder out = new StringBuilder();

        for (char c : s.toCharArray()) {
            if (!Character.isLetter(c))
                continue;
            if (!seen.contains(c)) {
                seen.add(c);
                out.append(c);
            }
        }

        return out.toString();
    }
}
