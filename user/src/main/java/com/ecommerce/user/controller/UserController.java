package com.ecommerce.user.controller;

import com.ecommerce.user.service.UserService;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    //private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @GetMapping()
    public ResponseEntity<List<UserResponse>> getAllUsers()
    {
        return ResponseEntity.ok().body(userService.fetchAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id)
    {
        log.info("request recieved fro the user :{}",id);
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
