package com.example.test.rmi.byteservice;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.example.test.domain.response.problem.ProblemResult;
import com.example.test.rmi.ByteService;
import com.example.test.service.SubmissionService;

@Component
public class ByteServiceImpl extends UnicastRemoteObject implements ByteService {

    private final SubmissionService submissionService;

    
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public ByteServiceImpl(SubmissionService submissionService) throws RemoteException {
        super();
        this.submissionService = submissionService;
    }

    
    @Override
    public byte[] requestData(String studentCode, String qCode) throws RemoteException {

        switch (qCode) {
            case "IqYxVvj8":
                String text = randomAsciiString(10 + new Random().nextInt(5));
                cache.put(studentCode + "_" + qCode, text);
                return text.getBytes();

            default:
                break;
        }
        return null;

    }

    
    @Override
    public void submitData(String studentCode, String qCode, byte[] data) throws RemoteException {

        switch (qCode) {
            case "IqYxVvj8":
                String s = cache.get(studentCode + "_" + qCode);

                byte[] originalBytes = s.getBytes();

                byte[] encoded = caesarEncode(originalBytes);

                String res = new String(encoded, 0, encoded.length);

                for (int i = 0; i < encoded.length; i++) {
                    if (encoded[i] != data[i]) {
                        
                        ProblemResult result = new ProblemResult();
                        result.setInputData(s);
                        result.setStudentResult(new String(data));
                        result.setExpectedResult(res);
                        result.setCorrect(false);
                        result.setStatus("Sai");
                        submissionService.save(studentCode, qCode, result);
                        return;
                    }
                }

                ProblemResult result = new ProblemResult();
                result.setInputData(s);
                result.setStudentResult(new String(data));
                result.setExpectedResult(res);
                result.setCorrect(true);
                result.setStatus("Đúng");
                submissionService.save(studentCode, qCode, result);
                break;

            default:
                break;
        }

    }

    private String randomAsciiString(int length) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            boolean upper = r.nextBoolean();
            if (upper) {
                sb.append((char) ('A' + r.nextInt(26)));
            } else {
                sb.append((char) ('a' + r.nextInt(26)));
            }
        }
        return sb.toString();
    }

    private static byte[] caesarEncode(byte[] data) {
        int shift = data.length;
        byte[] encoded = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            encoded[i] = (byte) (data[i] + shift);
        }
        return encoded;
    }

}
