package com.zunf.tankbattlebackend.controller;

import com.zunf.tankbattlebackend.common.BaseResp;
import com.zunf.tankbattlebackend.model.qo.user.AuthTokenQo;
import com.zunf.tankbattlebackend.model.qo.user.LoginQo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/login")
    public BaseResp<String> login(@RequestBody LoginQo qo) {
        return BaseResp.success("登录成功");
    }

    @PostMapping("/auth")
    public BaseResp<Void> authToken(@RequestBody AuthTokenQo qo) {
        return BaseResp.success();
    }
}
