package com.lframework.xingyun.core.queue.outbox;

public interface MqInboxDeduplicator {

  boolean accept(String eventId, String consumerName);
}
