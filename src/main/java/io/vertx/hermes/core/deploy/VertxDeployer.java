package io.vertx.hermes.core.deploy;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.hermes.core.HermesVerticle;
import io.vertx.reactivex.core.CompositeFuture;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Vertx;

import java.util.Arrays;
import java.util.stream.Collectors;

import static io.vertx.codegen.CodeGenProcessor.log;

public class VertxDeployer {

    private final Vertx vertx;

    public VertxDeployer(Vertx vertx) {
        this.vertx = vertx;
    }

    public Future<Void> deploy(Verticle... verticles) {
        final Future<Void> future = Future.future();
        CompositeFuture.all(
            Arrays.stream(verticles).map(this::deploy).collect(Collectors.toList()))
            .setHandler(result -> {
            if(result.succeeded()){
                future.complete();
            } else {
                future.fail(result.cause());
            }
        });
        return future;
    }

    public Observable<Boolean> rxDeploy(Verticle... verticles) {
        return Observable.fromArray(verticles)
                .flatMap(this::rxDeploy)
                .reduce((aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .toObservable();
    }

    public Observable<Boolean> rxDeploy(final Verticle verticle) {
        final String verticleName = getVerticleName(verticle);
        final DeploymentOptions options = new DeploymentOptions();
        return Single.just(verticle)
                .doOnSuccess(v -> { log.info("Try to Deploy verticle " + verticleName + "...");})
                .flatMap(v -> vertx.rxDeployVerticle(v, options))
                .map(deployrResult -> {
                    if (deployrResult != null && !deployrResult.isEmpty()) {
                        log.info("Deployed verticle " + verticleName);
                        return true;
                    }
                    else {
                        log.warning("Failed to deploy verticle " + verticleName);
                        return false;
                    }
                })
                .doOnError(throwable -> {
                    log.warning("Failed to deploy verticle " + verticleName);
                    log.throwing ("VertxDeployer", "deploy", throwable);
                })
                .toObservable();
    }

    private String getVerticleName(final Verticle verticle) {
       return HermesVerticle.class.isInstance(verticle) ?
                ((HermesVerticle) verticle).getName() :
                verticle.getClass().getSimpleName();
    }

    private Future<Void> deploy(Verticle verticle) {
        final String verticleName = getVerticleName(verticle);
        DeploymentOptions options = new DeploymentOptions();
        options.setInstances(1);
        final Future<Void> future = Future.future();
        vertx.deployVerticle(verticle, options, res -> {
            if(res.failed()){
                log.warning("Failed to deploy verticle " + verticleName);
                log.throwing ("VertxDeployer", "deploy", res.cause());
                future.fail(res.cause());
            } else {
                log.info("Deployed verticle " + verticleName);
                future.complete();
            }
        });
        return future;
    }
}
