package com.zunf.tankbattlebackend.manager;

import cn.hutool.core.util.ObjUtil;
import com.zunf.tankbattlebackend.common.ErrorCode;
import com.zunf.tankbattlebackend.grpc.room.GameRoomProto;
import com.zunf.tankbattlebackend.model.bo.GameRoomBo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class GameRoomManager {

    AtomicLong atomicInteger = new AtomicLong(0);

    Map<Long, GameRoomBo> gameRoomMap = new ConcurrentHashMap<>();

    public long createGameRoom(String roomName, int maxPlayerNum, Long creatorId) {
        long roomId = atomicInteger.getAndIncrement();
        GameRoomBo gameRoomBo = new GameRoomBo(roomId, creatorId, roomName, maxPlayerNum);
        gameRoomMap.put(roomId, gameRoomBo);
        return roomId;
    }

    public List<GameRoomBo> pageGameRoom(int pageNum, int pageSize) {
        return gameRoomMap.values().stream()
                .skip((long) (pageNum - 1) * pageSize)
                .limit(pageSize)
                .toList();
    }

    public GameRoomBo getGameRoom(long roomId) {
        return gameRoomMap.get(roomId);
    }

    /**
     * 加入房间
     * @param roomId 房间id
     * @param playerId 玩家id
     * @return code 0: 成功
     */
    public synchronized int joinGameRoom(long roomId, Long playerId) {
        GameRoomBo gameRoomBo = gameRoomMap.get(roomId);
        if (gameRoomBo == null) {
            return ErrorCode.GAME_ROOM_NOT_FOUND.getCode();
        }
        List<Long> curPlayerIds = gameRoomBo.getCurPlayerIds();
        if (curPlayerIds.contains(playerId)) {
            return ErrorCode.GAME_ROOM_PLAYER_EXIST.getCode();
        }
        if (curPlayerIds.size() >= gameRoomBo.getMaxPlayer()) {
            return ErrorCode.GAME_ROOM_FULL.getCode();
        }
        curPlayerIds.add(playerId);
        return ErrorCode.OK.getCode();
    }

    /**
     * 离开房间
     * @param roomId 房间id
     * @param playerId 玩家id
     * @return code 0: 成功
     */
    public synchronized int leaveGameRoom(long roomId, Long playerId) {
        GameRoomBo gameRoomBo = gameRoomMap.get(roomId);
        if (gameRoomBo == null) {
            return ErrorCode.GAME_ROOM_NOT_FOUND.getCode();
        }
        List<Long> curPlayerIds = gameRoomBo.getCurPlayerIds();
        if (!curPlayerIds.contains(playerId)) {
            return ErrorCode.GAME_ROOM_PLAYER_NOT_EXIST.getCode();
        }
        curPlayerIds.remove(playerId);
        // 如果是房主退出，则删除房间
        if (ObjUtil.equals(playerId, gameRoomBo.getCreatorId())) {
            gameRoomMap.remove(roomId);
        }
        return ErrorCode.OK.getCode();
    }

    /**
     * 房主开始游戏
     * @param roomId 房间id
     */
    public synchronized int startGame(long roomId, Long playerId) {
        GameRoomBo gameRoomBo = gameRoomMap.get(roomId);
        if (gameRoomBo == null) {
            return ErrorCode.GAME_ROOM_NOT_FOUND.getCode();
        }
        if (!ObjUtil.equals(playerId, gameRoomBo.getCreatorId())) {
            return ErrorCode.GAME_ROOM_NOT_CREATOR.getCode();
        }
        if (ObjUtil.equals(gameRoomBo.getRoomStatus(), GameRoomProto.RoomStatus.PLAYING)) {
            return ErrorCode.GAME_ROOM_ALREADY_START.getCode();
        }
        gameRoomBo.setRoomStatus(GameRoomProto.RoomStatus.PLAYING);
        return ErrorCode.OK.getCode();
    }
}
