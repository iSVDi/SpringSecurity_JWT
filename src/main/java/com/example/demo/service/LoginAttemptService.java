package com.example.demo.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.internal.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPT = 3;
    private final Map<String, Integer> attemptsCache = new HashMap<>();


    public void loginFailed(final String username) {
        int attempts = attemptsCache.get(username);
        attempts++;
        attemptsCache.put(username, attempts);
    }

    public boolean isBlocked(String username) {
        attemptsCache.putIfAbsent(username, 0);
        return attemptsCache.get(username) >= MAX_ATTEMPT;
    }


}
