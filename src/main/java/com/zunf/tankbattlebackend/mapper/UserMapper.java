package com.zunf.tankbattlebackend.mapper;

import com.zunf.tankbattlebackend.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author zunf
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2025-12-14 21:28:47
* @Entity com.zunf.tankbattlebackend.model.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




