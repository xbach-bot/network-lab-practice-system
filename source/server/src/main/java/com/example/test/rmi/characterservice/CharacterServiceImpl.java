package com.example.test.rmi.characterservice;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.example.test.domain.response.problem.ProblemResult;
import com.example.test.rmi.CharacterService;
import com.example.test.service.SubmissionService;

@Component
public class CharacterServiceImpl extends UnicastRemoteObject implements CharacterService {

    private final SubmissionService submissionService;

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public CharacterServiceImpl(SubmissionService submissionService) throws RemoteException {
        super();
        this.submissionService = submissionService;
    }


    @Override
    public String requestCharacter(String studentCode, String qCode) throws RemoteException {

        switch (qCode) {
            case "43BRpwcR":
                String text = randomAsciiString(10 + new Random().nextInt(5));
                cache.put(studentCode + "_" + qCode, text);
                return text;

            default:
                break;
        }
        return null;

    }

    
    @Override
    public void submitCharacter(String studentCode, String qCode, String data) throws RemoteException {

        switch (qCode) {
            case "43BRpwcR":
                String origin = cache.get(studentCode + "_" + qCode);

                String sv = buildFrequencyString(origin);

                if (!sv.equals(data)) {

                    ProblemResult result = new ProblemResult();
                    result.setInputData(origin);
                    result.setStudentResult(data);
                    result.setExpectedResult(sv);
                    result.setCorrect(false);
                    result.setStatus("Sai");
                    submissionService.save(studentCode, qCode, result);
                    return;
                }

                ProblemResult result = new ProblemResult();
                result.setInputData(origin);
                result.setStudentResult(data);
                result.setExpectedResult(sv);
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

            sb.append((char) ('A' + r.nextInt(26)));

        }
        return sb.toString();
    }

    private static String buildFrequencyString(String input) {
        Map<Character, Integer> freq = new LinkedHashMap<>();
        for (char c : input.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Character, Integer> e : freq.entrySet()) {
            sb.append(e.getKey()).append(e.getValue());
        }
        return sb.toString();
    }

}
