package com.example.test.handler.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.example.test.config.HandlerInfo;
import com.example.test.domain.response.problem.ProblemResult;
import com.example.test.handler.ProblemHandler;

@Component
@HandlerInfo(qCode = "0xhUgbny")
public class BinaryConvertHandler implements ProblemHandler {

    @Override
    public ProblemResult processTcpData(Socket socket,
            String studentCode,
            String qCode) throws Exception {

        socket.setSoTimeout(5000);

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        ProblemResult result = new ProblemResult();

        Random r = new Random();
        int number = 100 + r.nextInt(200);

        out.writeInt(number);
        out.flush();

        // STEP 3: chờ client gửi chuỗi nhị phân
        String studentBinary;
        try {
            studentBinary = in.readUTF().trim();
        } catch (SocketTimeoutException ste) {
            // client không trả lời trong thời gian timeout
            result.setInputData(number + "");
            result.setStudentResult("TimeLimitExceeded");
            result.setExpectedResult(Integer.toBinaryString(number));
            result.setCorrect(false);
            result.setStatus("Sai");
            return result;
        }

        // STEP 4: so sánh
        String expectedBinary = Integer.toBinaryString(number);
        boolean correct = expectedBinary.equals(studentBinary);

        result.setInputData(number + "");
        result.setStudentResult(studentBinary);
        result.setExpectedResult(expectedBinary);
        result.setCorrect(correct);
        result.setStatus(correct ? "Đúng" : "Sai");

        return result;

    }
}
