package com.lframework.xingyun.core.queue.outbox;

import java.time.LocalDateTime;

public record MqOutboxRecord(String id, MqOutboxEventType eventType, String payload,
                             Integer tenantId, int attempts, LocalDateTime createTime) {
}
