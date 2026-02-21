package com.ecommerce.user.controller;

import com.ecommerce.user.service.UserService;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<UserResponse>> getAllUsers()
    {
        return ResponseEntity.ok().body(userService.fetchAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id)
    {
        return userService.fetchUser(id)!=null?ResponseEntity.ok(userService.fetchUser(id)):ResponseEntity.notFound().build();
    }

    @PostMapping()
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest user)
    {
        return ResponseEntity.ok(userService.addUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> upadteUser(@PathVariable String id,@RequestBody UserRequest updatedUser)
    {
        return userService.updateUser(id,updatedUser)?
                ResponseEntity.ok("user updated successfully"):
                ResponseEntity.notFound().build();
    }
}
