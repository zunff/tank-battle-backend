package com.zunf.tankbattlebackend.controller;

import com.zunf.tankbattlebackend.common.BaseResp;
import com.zunf.tankbattlebackend.model.qo.user.LoginQo;
import com.zunf.tankbattlebackend.model.qo.user.RegisterQo;
import com.zunf.tankbattlebackend.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserServiceImpl userService;

    @PostMapping("/login")
    public BaseResp<String> login(@RequestBody LoginQo qo) {
        return BaseResp.success(userService.login(qo.getUsername(), qo.getPassword()));
    }

    @PostMapping("/register")
    public BaseResp<String> authToken(@RequestBody RegisterQo qo) {
        return BaseResp.success(userService.register(qo.getUsername(), qo.getPassword(), qo.getCheckPassword(), qo.getNickname()));
    }
}
