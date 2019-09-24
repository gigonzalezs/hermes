package io.vertx.hermes.core;

import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.hermes.core.exception.UndefinedConsumerChannelException;
import io.vertx.hermes.core.messaging.Channel;
import io.vertx.hermes.core.messaging.HermesMessage;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;

import java.util.Optional;

import static io.vertx.codegen.CodeGenProcessor.log;

public abstract class HermesConsumer extends AbstractVerticle implements HermesVerticle {

    protected final static String ACK = "ACK";
    protected final String name;
    protected Optional<Channel> consumerChannel;
    private EventBus eventBus;
    private final DeliveryOptions deliveryOptions = new DeliveryOptions();

    protected HermesConsumer() {
        this.name = this.getClass().getSimpleName();
        this.consumerChannel = Optional.empty();
        deliveryOptions.setCodecName("HermesCodec");
    }

    protected HermesConsumer(Channel consumerChannel) {
        this.name = this.getClass().getSimpleName();
        this.consumerChannel = Optional.of(consumerChannel);
        deliveryOptions.setCodecName("HermesCodec");
    }

    protected HermesConsumer(String name) {
        this.name = name;
        this.consumerChannel = Optional.empty();
        deliveryOptions.setCodecName("HermesCodec");
    }

    protected HermesConsumer(String name, Channel consumerChannel) {
        this.name = name;
        this.consumerChannel = Optional.of(consumerChannel);
        deliveryOptions.setCodecName("HermesCodec");
    }

    @Override
    public String getName() {
        return name;
    }

    public Channel getConsumerChannel() {
        return consumerChannel.orElse(null);
    }

    public void setConsumerChannel(Channel consumerChannel) {
        this.consumerChannel = Optional.of(consumerChannel);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public DeliveryOptions getDeliveryOptions() {return deliveryOptions;}

    @Override
    public void start(final Future<Void> future) {
        if (!consumerChannel.isPresent()) {
            throw new UndefinedConsumerChannelException();
        }
        eventBus = vertx.eventBus();
        eventBus.consumer(consumerChannel.get().getName(), this::handleEvent);
        log.info(String.format("Started HermesConsumer(%s) '%s': listening at: %s.",
                this.getClass().getName(), name, consumerChannel.get().getName()));
        loadOnInit();
        future.complete();
    }

    public void loadOnInit() {}

    @Override
    public void stop() {
        log.info(String.format("Shutting down HermesConsumer(%s): %s",
                this.getClass().getName(), name));
    }

    protected abstract void handleEvent(io.vertx.reactivex.core.eventbus.Message<HermesMessage> event);
}
