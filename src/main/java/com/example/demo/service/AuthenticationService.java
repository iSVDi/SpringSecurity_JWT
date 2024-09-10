package com.example.demo.service;

import com.example.demo.model.AuthenticationResponse;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;

    public AuthenticationResponse register(User request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        user = repository.save(user);
        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticate(User request) throws AuthenticationException {
        User user;
        try {
            user = repository.findByUsername(request.getUsername()).get();
        } catch (NoSuchElementException e) {
            throw new UsernameNotFoundException(request.getUsername() + " doesn't exist");
        }

        if (loginAttemptService.isBlocked(request.getUsername())) {
            repository.deleteByUsername(request.getUsername());
            throw new AccountExpiredException("User " + request.getUsername() + " is deleted because of a lot of wrong trying login operations");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(), request.getPassword()));
        } catch (AuthenticationException e) {
            loginAttemptService.loginFailed(request.getUsername());
            throw e;
        }

        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

}
