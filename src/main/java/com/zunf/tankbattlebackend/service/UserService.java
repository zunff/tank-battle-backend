package com.zunf.tankbattlebackend.service;

import com.zunf.tankbattlebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author zunf
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2025-12-14 21:28:47
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param checkPassword 确认密码
     * @param nickname 昵称
     * @return JWT token
     */
    String register(String username, String password, String checkPassword, String nickname);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return JWT token
     */
    String login(String username, String password);
}
