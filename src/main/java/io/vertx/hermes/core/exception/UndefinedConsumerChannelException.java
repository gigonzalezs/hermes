package io.vertx.hermes.core.exception;

public class UndefinedConsumerChannelException extends RuntimeException {
    public UndefinedConsumerChannelException() {
        super("Undefined consumer channel for HermesConsumer");
    }
}
