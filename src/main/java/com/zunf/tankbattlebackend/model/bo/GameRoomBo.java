package com.zunf.tankbattlebackend.model.bo;

import com.zunf.tankbattlebackend.grpc.room.GameRoomProto;
import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class GameRoomBo {

    private Long roomId;
    private String roomName;
    private Integer maxPlayer;

    private List<Long> curPlayerIds;
    private Long creatorId;

    private GameRoomProto.RoomStatus roomStatus;

    public GameRoomBo(Long roomId, Long creatorId, String roomName, Integer maxPlayer) {
        this.roomId = roomId;
        this.creatorId = creatorId;
        this.roomName = roomName;
        this.maxPlayer = maxPlayer;
        this.curPlayerIds = new CopyOnWriteArrayList<>();
        this.roomStatus = GameRoomProto.RoomStatus.WAITING;
    }
}
