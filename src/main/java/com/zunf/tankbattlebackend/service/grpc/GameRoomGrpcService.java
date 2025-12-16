package com.zunf.tankbattlebackend.service.grpc;

import com.zunf.tankbattlebackend.common.ErrorCode;
import com.zunf.tankbattlebackend.grpc.CommonProto;
import com.zunf.tankbattlebackend.grpc.room.GameRoomProto;
import com.zunf.tankbattlebackend.grpc.room.GameRoomServiceGrpc;
import com.zunf.tankbattlebackend.manager.GameRoomManager;
import com.zunf.tankbattlebackend.model.bo.GameRoomBo;
import com.zunf.tankbattlebackend.model.entity.User;
import com.zunf.tankbattlebackend.service.UserService;
import com.zunf.tankbattlebackend.utils.ProtoBufUtil;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import javax.annotation.Resource;
import java.util.List;

@GrpcService
public class GameRoomGrpcService extends GameRoomServiceGrpc.GameRoomServiceImplBase{

    @Resource
    private GameRoomManager gameRoomManager;

    @Resource
    private UserService userService;

    @Override
    public void createGameRoom(GameRoomProto.CreateGameRoomRequest request, StreamObserver<CommonProto.BaseResponse> responseObserver) {
        long roomId = gameRoomManager.createGameRoom(request.getName(), request.getMaxPlayers(), request.getPlayerId());
        responseObserver.onNext(ProtoBufUtil.successResp(GameRoomProto.CreateGameRoomResponse.newBuilder().setRoomId(roomId).build().toByteString()));
        responseObserver.onCompleted();
    }

    @Override
    public void pageGameRoom(GameRoomProto.PageRequest request, StreamObserver<CommonProto.BaseResponse> responseObserver) {
        List<GameRoomBo> gameRoomBos = gameRoomManager.pageGameRoom(request.getPageNum(), request.getPageSize());
        List<GameRoomProto.GameRoomData> roomList = gameRoomBos.stream().map(gameRoomBo -> GameRoomProto.GameRoomData.newBuilder()
                .setId(gameRoomBo.getRoomId())
                .setName(gameRoomBo.getRoomName())
                .setMaxPlayers(gameRoomBo.getMaxPlayer())
                .setStatus(gameRoomBo.getRoomStatus()).build()).toList();
        responseObserver.onNext(ProtoBufUtil.successResp(GameRoomProto.PageResponse.newBuilder().addAllData(roomList).setTotal(gameRoomBos.size()).build().toByteString()));
        responseObserver.onCompleted();
    }

    @Override
    public void joinGameRoom(GameRoomProto.JoinGameRoomRequest request, StreamObserver<CommonProto.BaseResponse> responseObserver) {
        int code = gameRoomManager.joinGameRoom(request.getRoomId(), request.getPlayerId());
        if (code != ErrorCode.OK.getCode()) {
            responseObserver.onNext(ProtoBufUtil.baseCodeResp(ErrorCode.of(code)));
            responseObserver.onCompleted();
        }
        // 查询房间信息
        GameRoomBo gameRoomBo = gameRoomManager.getGameRoom(request.getRoomId());
        List<GameRoomProto.GameRoomPlayerData> playerDataList = userService.lambdaQuery().in(User::getId, gameRoomBo.getCurPlayerIds()).list().stream().map(user -> GameRoomProto.GameRoomPlayerData.newBuilder()
                .setPlayerId(user.getId())
                .setNickName(user.getNickname())
                .build()).toList();
        GameRoomProto.GameRoomDetail gameRoomDetail = GameRoomProto.GameRoomDetail.newBuilder()
                .setId(gameRoomBo.getRoomId())
                .setName(gameRoomBo.getRoomName())
                .setMaxPlayers(gameRoomBo.getMaxPlayer())
                .setStatus(gameRoomBo.getRoomStatus())
                .setCreatorId(gameRoomBo.getCreatorId())
                .addAllPlayers(playerDataList).build();
        responseObserver.onNext(ProtoBufUtil.successResp(gameRoomDetail.toByteString()));
        // todo 推送房间信息给房间内所有玩家
        responseObserver.onCompleted();
    }

    @Override
    public void leaveGameRoom(GameRoomProto.LeaveGameRoomRequest request, StreamObserver<CommonProto.BaseResponse> responseObserver) {
        int code = gameRoomManager.leaveGameRoom(request.getRoomId(), request.getPlayerId());
        responseObserver.onNext(ProtoBufUtil.baseCodeResp(ErrorCode.of(code)));
        responseObserver.onCompleted();
    }

    @Override
    public void startGameRoom(GameRoomProto.StartGameRoomRequest request, StreamObserver<CommonProto.BaseResponse> responseObserver) {
        int code = gameRoomManager.startGame(request.getRoomId(), request.getPlayerId());
        responseObserver.onNext(ProtoBufUtil.baseCodeResp(ErrorCode.of(code)));
        responseObserver.onCompleted();
    }
}
