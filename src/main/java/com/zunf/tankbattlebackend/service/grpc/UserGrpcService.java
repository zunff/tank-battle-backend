package com.zunf.tankbattlebackend.service.grpc;

import cn.hutool.core.collection.CollUtil;
import com.zunf.tankbattlebackend.StressTestUserManager;
import com.zunf.tankbattlebackend.common.ErrorCode;
import com.zunf.tankbattlebackend.grpc.CommonProto;
import com.zunf.tankbattlebackend.grpc.user.UserProto;
import com.zunf.tankbattlebackend.grpc.user.UserServiceGrpc;
import com.zunf.tankbattlebackend.model.entity.User;
import com.zunf.tankbattlebackend.service.UserService;
import com.zunf.tankbattlebackend.utils.ProtoBufUtil;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import javax.annotation.Resource;
import java.util.List;

@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    @Resource
    private UserService userService;

    @Resource
    private StressTestUserManager stressTestUserManager;

    @Override
    public void getUser(UserProto.GetUserRequest request, StreamObserver<CommonProto.BaseResponse> responseObserver) {
        long playerId = request.getPlayerId();
        if (playerId <= 0) {
            responseObserver.onNext(ProtoBufUtil.baseCodeResp(ErrorCode.INVALID_ARGUMENT));
            responseObserver.onCompleted();
        }
        if (stressTestUserManager.isStressTestUser(playerId)) {
            String userName = stressTestUserManager.getUserName(playerId);
            UserProto.GetUserResponse resp = UserProto.GetUserResponse.newBuilder()
                    .setUser(UserProto.UserInfo.newBuilder().setPlayerId(playerId).setNickname(userName).build())
                    .build();
            responseObserver.onNext(ProtoBufUtil.successResp(resp.toByteString()));
            responseObserver.onCompleted();
            return;
        }
        User user = userService.getById(playerId);
        if (user == null) {
            responseObserver.onNext(ProtoBufUtil.baseCodeResp(ErrorCode.NOT_FOUND));
            responseObserver.onCompleted();
        }
        UserProto.GetUserResponse resp = UserProto.GetUserResponse.newBuilder()
                .setUser(UserProto.UserInfo.newBuilder().setPlayerId(user.getId()).setNickname(user.getNickname()).build())
                .build();
        responseObserver.onNext(ProtoBufUtil.successResp(resp.toByteString()));
        responseObserver.onCompleted();
    }

    @Override
    public void listUser(UserProto.ListUserRequest request, StreamObserver<CommonProto.BaseResponse> responseObserver) {
        List<Long> playerIdsList = request.getPlayerIdsList();
        if (CollUtil.isEmpty(playerIdsList)) {
            responseObserver.onNext(ProtoBufUtil.baseCodeResp(ErrorCode.INVALID_ARGUMENT));
        }
        List<User> userList = userService.listByIds(playerIdsList);
        List<UserProto.UserInfo> userInfoList = userList.stream().map(user -> UserProto.UserInfo.newBuilder().setPlayerId(user.getId()).setNickname(user.getNickname()).build()).toList();
        UserProto.ListUserResponse resp = UserProto.ListUserResponse.newBuilder().addAllUsers(userInfoList).build();
        responseObserver.onNext(ProtoBufUtil.successResp(resp.toByteString()));
        responseObserver.onCompleted();
    }
}
