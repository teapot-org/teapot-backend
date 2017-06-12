package org.teapot.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.teapot.backend.repository.UserRepository;
import org.teapot.backend.model.User;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/{id}")
    public User getTestUser(@PathVariable Long id) {
        return userRepository.findOne(id);
    }
}
