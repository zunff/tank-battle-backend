package com.zunf.tankbattlebackend.model.qo.user;

import lombok.Data;

@Data
public class RegisterQo {

    private String username;

    private String password;

    private String checkPassword;
    
    private String nickname;
}
