package io.vertx.hermes.core.messaging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Single;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HermesMessage {

    @JsonIgnore
    private Map<String,String> headers = new HashMap<>();
    @JsonIgnore
    Future future = Future.future();
    @JsonIgnore
    private Optional<Object> payload;

    public HermesMessage() {
    }

    public HermesMessage(Object payload) {
        setPayload(payload);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setPayload(Object payload) {
        this.payload = Optional.ofNullable(payload);
        if(!future.isComplete()) {
            future.complete(this);
        }
    }

    public void onMessageCompleted(Handler<Single<HermesMessage>> handler) {
        future.setHandler(result -> {
            Single<HermesMessage> event = Single.just(this);
            handler.handle(event);
        });
    }

    @JsonIgnore
    public Single<String> rxToJson() {
        return Single.defer(() -> {
            ObjectMapper mapper = new ObjectMapper();
            return Single.just(mapper.writeValueAsString(this));
        });
    }

    @JsonIgnore
    public String toJson() {
            ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HermesMessage fromJson(String json) {
        return fromJson(json,null);
    }

    public static HermesMessage fromJson(String json, Class<?> bodyClass) {
        ObjectMapper mapper = new ObjectMapper();
        HermesMessage hermesMessage = null;
        try {
            hermesMessage = mapper.readValue(json, HermesMessage.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (bodyClass != null) {
            Object body = deserializeBody(json, bodyClass);
            hermesMessage.setPayload(body);
        }
        return hermesMessage;
    }

    private static Object deserializeBody(String json, Class<?> bodyClass) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode messageNode = null;
        Object result = null;
        try {
            messageNode = mapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonNode bodyNode = messageNode.get("body");
        String bodyJson = bodyNode.toString();
        try {
            result = mapper.readValue(bodyJson, bodyClass);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    @JsonProperty("headers")
    public Map<String,String> getHeaders() {
        return headers;
    }

    @JsonProperty("body")
    public Object getPayload() {

        try {
            Object result = this.payload.orElse(null);
            if (JsonObject.class.isInstance(result)) {
                result = ((JsonObject)result).getMap();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @JsonIgnore
    public String getPayLoadAsJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(getPayload());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
