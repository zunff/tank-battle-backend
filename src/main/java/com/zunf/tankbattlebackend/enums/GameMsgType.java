package com.zunf.tankbattlebackend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum GameMsgType {
    // server -> client
    PLAYER_JOIN_ROOM(1002),
    PLAYER_LEAVE_ROOM(1003),

    // common
    ERROR(0),
    UNKNOWN(2048);

    private final int code;

    public static GameMsgType of(int code) {
        return Arrays.stream(values()).filter(v -> v.code == code).findFirst().orElse(UNKNOWN);
    }
}