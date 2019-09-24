package io.vertx.hermes.core;

import io.reactivex.Observable;
import io.vertx.core.Verticle;
import io.vertx.hermes.core.deploy.VertxDeployer;
import io.vertx.reactivex.core.Vertx;

import java.util.ArrayList;
import java.util.List;

public abstract class HermesModule {

    // -- framework
    private final VertxDeployer deployer;
    protected final Vertx vertx;

    // -- verticles
    private List<Verticle> verticles;

    private final String name;

    public HermesModule(Vertx vertx, String name) {
        this.vertx = vertx;
        this.name = name;
        this.deployer = new VertxDeployer(vertx);
        this.verticles = new ArrayList<>();
    }

    public Observable<Boolean> rxDeploy() {
        System.out.println(String.format("Starting %s module...", name));
        setupServices();
        this.verticles = setupVerticles();
        return deployer.rxDeploy(verticles.toArray(new Verticle[0]))
                .doOnComplete(() -> onDeployComplete());
    }

    protected void onDeployComplete() {}

    protected abstract void setupServices();

    protected abstract ArrayList<Verticle> setupVerticles();
}
