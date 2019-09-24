package io.vertx.hermes.core.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;

public class MessageDispatcher {

    private final EventBus eventBus;
    private final DeliveryOptions deliveryOptions = new DeliveryOptions();

    public MessageDispatcher(Vertx vertx) {
        eventBus = vertx.eventBus();
        deliveryOptions.setCodecName("HermesCodec");
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public Observable<Message<HermesMessage>> rxSend(Channel channel, Object payload ) {
        return Observable.just(payload)
                .map(HermesMessage::new)
                .flatMap(this::configureHermesMessage)
                .flatMap(message -> eventBus.<HermesMessage>rxSend(
                        channel.getName(), message, deliveryOptions)
                        .toObservable())
                ;

        /*
        HermesMessage hermesMessage = new HermesMessage(payload);
        return eventBus.<HermesMessage>rxSend(channel.getName(), hermesMessage, deliveryOptions)
                .toObservable();
                */
    }

    public Observable<Message<HermesMessage>> rxSend(Channel channel, HermesMessage message ) {
        return Observable.just(message)
                .flatMap(mgs -> eventBus.<HermesMessage>rxSend(
                        channel.getName(), mgs, deliveryOptions)
                        .toObservable())
                ;

        /*
        HermesMessage hermesMessage = new HermesMessage(payload);
        return eventBus.<HermesMessage>rxSend(channel.getName(), hermesMessage, deliveryOptions)
                .toObservable();
                */
    }

    protected Observable<HermesMessage> configureHermesMessage(HermesMessage message) {
        return Observable.just(message);
    }
}
