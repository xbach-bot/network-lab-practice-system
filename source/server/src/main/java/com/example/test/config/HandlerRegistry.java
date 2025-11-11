package com.example.test.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.test.handler.ProblemHandler;

@Component
public class HandlerRegistry {

    private final Map<String, ProblemHandler> registry = new HashMap<>();

    public HandlerRegistry(List<ProblemHandler> handlers) {
        handlers.forEach(h -> {
            HandlerInfo info = h.getClass().getAnnotation(HandlerInfo.class);
            if (info != null) {
                registry.put(info.qCode(), h);
            }
        });
    }

    public ProblemHandler get(String qCode) {
        return registry.get(qCode);
    }
}
