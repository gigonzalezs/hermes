package io.vertx.hermes.core.exception;

public class UndefinedProducerChannelException extends RuntimeException {
    public UndefinedProducerChannelException() {
        super("Undefined producer channel for HermesProducer");
    }
}
