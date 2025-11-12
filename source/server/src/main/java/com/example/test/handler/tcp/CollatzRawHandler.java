package com.example.test.handler.tcp;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.example.test.config.HandlerInfo;
import com.example.test.domain.response.problem.ProblemResult;
import com.example.test.handler.ProblemHandler;

@Component
@HandlerInfo(qCode = "BDF0CKv5")
public class CollatzRawHandler implements ProblemHandler {

    @Override
    public ProblemResult processTcpRaw(
            Socket socket,
            String studentCode,
            String qCode) throws Exception {

        socket.setSoTimeout(5000);

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        int n = new Random().nextInt(399) + 1;

        String nStr = String.valueOf(n);
        out.write(nStr.getBytes());
        out.flush();
        String received = null;
        try {

            byte[] buffer = new byte[2048];
            int len = in.read(buffer);

            received = new String(buffer, 0, len).trim();
        } catch (Exception e) {

            ProblemResult result = new ProblemResult();
            result.setInputData(String.valueOf(n));
            result.setStudentResult("TimeLimitExceeded");
            result.setExpectedResult(generateCollatzString(n));
            result.setCorrect(false);
            result.setStatus("Sai");
            return result;
        }

        String expected = generateCollatzString(n);

        boolean correct = expected.trim().equals(received);

        ProblemResult result = new ProblemResult();
        result.setInputData(String.valueOf(n));
        result.setStudentResult(received);
        result.setExpectedResult(expected);
        result.setCorrect(correct);
        result.setStatus(correct ? "Đúng" : "Sai");

        return result;
    }

    private String generateCollatzString(int nStart) {
        int n = nStart;
        StringBuilder sb = new StringBuilder();
        int count = 0;

        while (true) {
            sb.append(n).append(" ");
            count++;

            if (n == 1)
                break;

            if (n % 2 == 0)
                n = n / 2;
            else
                n = 3 * n + 1;
        }

        return sb.toString().trim() + "; " + count + ";";
    }
}
