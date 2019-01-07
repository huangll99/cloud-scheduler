package com.gsafety.scheduler.controller;

import com.gsafety.scheduler.model.Result;
import com.gsafety.scheduler.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: huangll
 * Written on 2019-01-07.
 */
@RestController
public class LoginController {

    @Value("${scheduler.username}")
    private String username;

    @Value("${scheduler.password}")
    private String password;

    @PostMapping("/login")
    public Result login(@RequestBody User user){
        if (username.equals(user.getUsername()) && password.equals(user.getPassword())){
            return Result.builder().success(true).msg("ok").build();
        }
        return Result.builder().success(false).msg("username or password is wrong").build();
    }
}
