package com.zunf.tankbattlebackend.service.grpc;

import com.zunf.tankbattlebackend.common.ErrorCode;
import com.zunf.tankbattlebackend.grpc.CommonProto;
import com.zunf.tankbattlebackend.grpc.auth.AuthProto;
import com.zunf.tankbattlebackend.grpc.auth.AuthServiceGrpc;
import com.zunf.tankbattlebackend.model.entity.User;
import com.zunf.tankbattlebackend.service.impl.UserServiceImpl;
import com.zunf.tankbattlebackend.utils.JwtUtils;
import com.zunf.tankbattlebackend.utils.ProtoBufUtil;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import javax.annotation.Resource;

@GrpcService
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {

    @Resource
    private UserServiceImpl userService;

    @Override
    public void checkToken(AuthProto.CheckTokenRequest request, StreamObserver<CommonProto.BaseResponse> responseObserver) {
        String token = request.getToken();
        Long userId = JwtUtils.validateToken(token);
        CommonProto.BaseResponse response;
        if (userId == null) {
            response = ProtoBufUtil.failResp(ErrorCode.UNAUTHORIZED);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }
        User user = userService.lambdaQuery().eq(User::getId, userId).one();
        if (user == null) {
            response = ProtoBufUtil.failResp(ErrorCode.UNAUTHORIZED);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }
        response = ProtoBufUtil.successResp(AuthProto.CheckTokenResponse.newBuilder().setPlayerId(userId).build().toByteString());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
