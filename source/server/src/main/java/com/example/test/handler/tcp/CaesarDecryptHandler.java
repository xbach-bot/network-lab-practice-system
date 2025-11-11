package com.example.test.handler.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.example.test.config.HandlerInfo;
import com.example.test.domain.response.problem.ProblemResult;
import com.example.test.handler.ProblemHandler;

@Component
@HandlerInfo(qCode = "x77snUdo") // ánh xạ vào HandlerRegistry
public class CaesarDecryptHandler implements ProblemHandler {

    /**
     * Bài TCP dạng DATA:
     * 1. Server sinh text và shift
     * 2. Server mã hóa text → gửi encrypted + shift cho client
     * 3. Client giải mã → gửi decrypted
     * 4. Server kiểm tra đúng sai
     */
    @Override
    public ProblemResult processTcpData(Socket socket,
            String studentCode,
            String qCode) throws Exception {

        socket.setSoTimeout(5000);

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        // STEP 1: sinh text và shift
        String text = randomText(12 + new Random().nextInt(9));
        int shift = new Random().nextInt(5) + 1;

        // mã hóa text (Caesar)
        String encrypted = encrypt(text, shift);

        // STEP 2: gửi encrypted + shift xuống client
        out.writeUTF(encrypted);
        out.writeInt(shift);
        out.flush();

        // STEP 3: nhận kết quả giải mã từ client
        String studentResult;
        try {
            studentResult = in.readUTF().trim();
        } catch (Exception e) {
            // client không trả lời trong thời gian timeout
            ProblemResult result = new ProblemResult();
            result.setInputData(encrypted + ";" + shift);
            result.setStudentResult("TimeLimitExceeded");
            result.setExpectedResult(text);
            result.setCorrect(false);
            result.setStatus("Sai");
            return result;
        }

        // STEP 4: server giải mã để tạo expected
        String expected = decrypt(encrypted, shift);

        boolean correct = expected.equals(studentResult);

        // trả về ProblemResult đầy đủ
        ProblemResult result = new ProblemResult();
        result.setInputData(encrypted + ";" + shift); // lưu input để log
        result.setStudentResult(studentResult);
        result.setExpectedResult(expected);
        result.setCorrect(correct);
        result.setStatus(correct ? "Đúng" : "Sai");

        return result;
    }

    /** Mã hóa Caesar (server gửi xuống client) */
    private String encrypt(String text, int shift) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            char e = (char) ((c - 'A' + shift) % 26 + 'A');
            sb.append(e);
        }
        return sb.toString();
    }

    /** Giải mã Caesar (server để kiểm tra) */
    private String decrypt(String enc, int shift) {
        StringBuilder sb = new StringBuilder();
        for (char c : enc.toCharArray()) {
            char d = (char) ((c - 'A' - shift + 26) % 26 + 'A');
            sb.append(d);
        }
        return sb.toString();
    }

    private String randomText(int length) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = (char) ('A' + r.nextInt(26));
            sb.append(c);
        }
        return sb.toString();
    }
}
