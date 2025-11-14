package com.example.test.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.springframework.stereotype.Component;

import com.example.test.rmi.byteservice.ByteServiceImpl;
import com.example.test.rmi.characterservice.CharacterServiceImpl;
import com.example.test.service.SubmissionService;

import jakarta.annotation.PostConstruct;

@Component
public class RmiServer {

    private final SubmissionService submissionService;

    public RmiServer(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostConstruct
    public void startRmi() {
        try {
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");

            ByteServiceImpl byteService = new ByteServiceImpl(submissionService);

            CharacterServiceImpl characterService = new CharacterServiceImpl(submissionService);

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("RMIByteService", byteService);
            registry.rebind("RMICharacterService", characterService);

            System.out.println("RMI server is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
