package com.events.controller;

import com.events.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin()
public class UserController {

    private final UserService userService;


    @PostMapping("/user/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id){
        return userService.deleteUser(id);
    }




}