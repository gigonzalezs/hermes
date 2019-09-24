package io.vertx.hermes.core;

import io.vertx.core.Verticle;
import io.vertx.reactivex.core.eventbus.EventBus;

public interface HermesVerticle extends Verticle {

    String getName();
    EventBus getEventBus();
}
