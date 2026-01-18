package com.zunf.tankbattlebackend;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.zunf.tankbattlebackend.utils.JwtUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StressTestUserManager {

    Map<Long, String> userMap = new ConcurrentHashMap<>();

    Snowflake snowflake = IdUtil.getSnowflake();

    public String createStressTestToken() {
        long id = snowflake.nextId();
        userMap.put(id, "stress_test_user_" + id);
        return JwtUtils.generateToken(id);
    }

    public String getUserName(Long id) {
        return userMap.get(id);
    }

    public boolean isStressTestUser(Long id) {
        return userMap.containsKey(id);
    }


}
