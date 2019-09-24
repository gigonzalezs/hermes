package io.vertx.hermes.core.messaging;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.reactivex.core.Vertx;

public class HermesCodec implements MessageCodec<HermesMessage, HermesMessage> {

    @Override
    public void encodeToWire(Buffer buffer, HermesMessage hermesMessage) {
        String jsonToStr = hermesMessage.toJson();
        int length = jsonToStr.getBytes().length;
        buffer.appendInt(length);
        buffer.appendString(jsonToStr);
    }

    @Override
    public HermesMessage decodeFromWire(int pos, Buffer buffer) {
        int _pos = pos;
        int length = buffer.getInt(_pos);
        String jsonStr = buffer.getString(_pos+=4, _pos+=length);
        return HermesMessage.fromJson(jsonStr);
    }

    @Override
    public HermesMessage transform(HermesMessage hermesMessage) {
        return hermesMessage;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }

    public static void registerCodec(Vertx vertx) {
        vertx.eventBus().registerCodec(new HermesCodec());
    }
}
