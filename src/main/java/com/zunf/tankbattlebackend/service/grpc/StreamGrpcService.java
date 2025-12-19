package com.zunf.tankbattlebackend.service.grpc;

import com.google.protobuf.ByteString;
import com.zunf.tankbattlebackend.grpc.stream.PushServiceGrpc;
import com.zunf.tankbattlebackend.grpc.stream.StreamProto;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.concurrent.atomic.AtomicReference;

@GrpcService
public class StreamGrpcService extends PushServiceGrpc.PushServiceImplBase {

    /**
     * 只有一个 tcpserver：保存一个订阅者即可
     */
    private final AtomicReference<ServerCallStreamObserver<StreamProto.PushMessage>> subscriber = new AtomicReference<>();

    @Override
    public void subscribe(StreamProto.SubscribeRequest request, StreamObserver<StreamProto.PushMessage> responseObserver) {

        // 转成 ServerCallStreamObserver 才能监听 cancel/断开
        ServerCallStreamObserver<StreamProto.PushMessage> serverObs = (ServerCallStreamObserver<StreamProto.PushMessage>) responseObserver;

        // 可选：如果你希望同一时间只允许一个 tcpserver 订阅
        ServerCallStreamObserver<StreamProto.PushMessage> old = subscriber.getAndSet(serverObs);
        if (old != null && old != serverObs) {
            // 旧连接踢掉
            try {
                old.onCompleted();
            } catch (Exception ignore) {
            }
        }

        // 连接断开/客户端取消时清理
        serverObs.setOnCancelHandler(() -> {
            subscriber.compareAndSet(serverObs, null);
        });

        // 可选：也可以设置 onClose/onReady handler
        // serverObs.setOnReadyHandler(() -> { ... });

        // server-streaming：这里不要 onCompleted()，保持流不断开
        // 也不需要立刻 onNext()，除非想发个“订阅成功”消息
    }

    /**
     * 业务侧调用：推送给某个 playerId
     */
    public boolean pushToPlayer(long playerId, byte[] payload) {
        ServerCallStreamObserver<StreamProto.PushMessage> obs = subscriber.get();
        if (obs == null) return false;          // tcpserver 未订阅/已断开
        if (obs.isCancelled()) {                // 已取消则清理
            subscriber.compareAndSet(obs, null);
            return false;
        }

        StreamProto.PushMessage msg = StreamProto.PushMessage.newBuilder()
                .setPlayerId(playerId)
                .setPayload(ByteString.copyFrom(payload))
                .build();

        try {
            // 注意：onNext 可能抛异常（例如对端断开）
            obs.onNext(msg);
            return true;
        } catch (Exception e) {
            subscriber.compareAndSet(obs, null);
            return false;
        }
    }
}