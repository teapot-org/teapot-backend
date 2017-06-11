package org.teapot.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.teapot.backend.controller.exception.BadRequestException;
import org.teapot.backend.controller.exception.ResourceNotFoundException;
import org.teapot.backend.model.User;
import org.teapot.backend.service.abstr.UserService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getUsers(@RequestParam(required = false) Integer offset,
                               @RequestParam(required = false) Integer count) {
        List<User> users;
        if ((offset != null) && (count != null)) {
            users = userService.getList(offset, count);
        } else {
            users = userService.getList();
        }
        return users;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new ResourceNotFoundException();
        }
        return user;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable Long id,
                        @RequestBody User user) {
        System.out.println();
        if (userService.getById(id) == null) {
            throw new ResourceNotFoundException();
        }
        user.setId(id);
        userService.update(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        if (userService.getById(id) == null) {
            throw new ResourceNotFoundException();
        }
        userService.delete(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@RequestBody User user,
                             HttpServletResponse response) {
        try {
            userService.register(user);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException();
        }
        response.setHeader("Location", "/api/users/" + user.getId());
        return user;
    }
}
