package com.zunf.tankbattlebackend.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zunf.tankbattlebackend.common.BusinessException;
import com.zunf.tankbattlebackend.common.ErrorCode;
import com.zunf.tankbattlebackend.model.entity.User;
import com.zunf.tankbattlebackend.service.UserService;
import com.zunf.tankbattlebackend.mapper.UserMapper;
import com.zunf.tankbattlebackend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author zunf
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2025-12-14 21:28:47
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Override
    public String register(String username, String password, String checkPassword, String nickname) {
        // 参数校验
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password) || StrUtil.isBlank(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if (username.length() < 3 || username.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名长度必须在3-20个字符之间");
        }
        if (password.length() < 6 || password.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度必须在6-20个字符之间");
        }
        if (!password.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getUsername, username);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名已存在");
        }

        // 密码加密
        String encryptedPassword = DigestUtil.md5Hex(password);

        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptedPassword);
        user.setNickname(StrUtil.isNotBlank(nickname) ? nickname : username); // 如果nickname为空，则默认使用username

        // 保存用户
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "注册失败");
        }

        // 生成JWT token
        return JwtUtils.generateToken(user.getId());
    }

    @Override
    public String login(String username, String password) {
        // 参数校验
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if (username.length() < 3 || username.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名长度必须在3-20个字符之间");
        }
        if (password.length() < 6 || password.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度必须在6-20个字符之间");
        }

        // 根据用户名查找用户
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getUsername, username);
        User user = this.getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }

        // 验证密码
        String encryptedPassword = DigestUtil.md5Hex(password);
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }

        // 生成JWT token
        return JwtUtils.generateToken(user.getId());
    }
}




