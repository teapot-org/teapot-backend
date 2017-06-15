package org.teapot.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.teapot.backend.controller.exception.BadRequestException;
import org.teapot.backend.controller.exception.ResourceNotFoundException;
import org.teapot.backend.model.User;
import org.teapot.backend.repository.UserRepository;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).getContent();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        User user = userRepository.findOne(id);
        if (user == null) {
            throw new ResourceNotFoundException();
        }
        return user;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable Long id,
                           @RequestBody User user) {
        if (!userRepository.exists(id)) {
            throw new ResourceNotFoundException();
        }
        user.setId(id);
        userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        if (!userRepository.exists(id)) {
            throw new ResourceNotFoundException();
        }
        userRepository.delete(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@RequestBody User user,
                             HttpServletResponse response) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new BadRequestException();
        }

        user = userRepository.save(user);
        response.setHeader("Location", "/users/" + user.getId());
        return user;
    }
}
