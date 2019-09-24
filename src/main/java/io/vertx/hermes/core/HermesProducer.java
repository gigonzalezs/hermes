package io.vertx.hermes.core;

import io.vertx.core.Future;
import io.vertx.hermes.core.exception.UndefinedProducerChannelException;
import io.vertx.hermes.core.messaging.Channel;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;

import java.util.Optional;

import static io.vertx.codegen.CodeGenProcessor.log;

public abstract class HermesProducer extends AbstractVerticle implements HermesVerticle {

    protected final String name;
    protected Optional<Channel> producerChannel;
    private EventBus eventBus;

    protected HermesProducer() {
        this.name = this.getClass().getSimpleName();
        this.producerChannel = Optional.empty();
    }

    protected HermesProducer(Channel producerChannel) {
        this.name = this.getClass().getSimpleName();
        this.producerChannel = Optional.of(producerChannel);
    }

    protected HermesProducer(String name) {
        this.name = name;
        this.producerChannel = Optional.empty();
    }

    protected HermesProducer(String name, Channel producerChannel) {
        this.name = name;
        this.producerChannel = Optional.of(producerChannel);
    }

    @Override
    public String getName() {
        return name;
    }

    public Channel getProducerChannel() {
        return producerChannel.orElse(null);
    }

    public void setProducerChannel(Channel producerChannel) {
        this.producerChannel = Optional.of(producerChannel);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public void start(final Future<Void> future) {
        if (!producerChannel.isPresent()) {
            throw new UndefinedProducerChannelException();
        }
        eventBus = vertx.eventBus();
        Future f = Future.future(event -> {
            log.info(String.format("Started HermesProducer(%s) '%s': publishing at: %s.",
                    this.getClass().getName(), name, producerChannel.get().getName()));
            future.complete();
        });
        init(f);
    }

    public abstract void init(final Future<Void> future);
}
