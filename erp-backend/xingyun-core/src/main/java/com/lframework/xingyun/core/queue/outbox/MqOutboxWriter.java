package com.lframework.xingyun.core.queue.outbox;

import java.io.Serializable;

public interface MqOutboxWriter {

  String append(MqOutboxEventType eventType, Serializable payload);
}
