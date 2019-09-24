package io.vertx.hermes.core.messaging;

public interface Channel {

    String getName();

    static Channel fromName(String name) {
        return new ChannelImpl(name);
    }

    class ChannelImpl implements Channel {

        private final String name;

        public ChannelImpl(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}
