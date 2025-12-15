package com.zunf.tankbattlebackend.utils;

import com.google.protobuf.ByteString;
import com.zunf.tankbattlebackend.common.ErrorCode;
import com.zunf.tankbattlebackend.grpc.CommonProto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProtoBufUtil {

    public static CommonProto.BaseResponse successResp(ByteString body) {
        return CommonProto.BaseResponse.newBuilder().setCode(ErrorCode.OK.getCode()).setMessage(ErrorCode.OK.getMessage()).setPayloadBytes(body).build();
    }

    public static CommonProto.BaseResponse failResp(ErrorCode errorCode) {
        return CommonProto.BaseResponse.newBuilder().setCode(errorCode.getCode()).setMessage(errorCode.getMessage()).build();
    }
}
