package com.lframework.xingyun.core.queue.outbox;

import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqOutboxRelayService {

  private static final int MAX_ERROR_LENGTH = 1000;

  private final JdbcMqOutboxStore store;
  private final MqOutboxPublisher publisher;
  private final MqOutboxProperties properties;

  public MqOutboxRelayService(JdbcMqOutboxStore store, MqOutboxPublisher publisher,
      MqOutboxProperties properties) {
    this.store = store;
    this.publisher = publisher;
    this.properties = properties;
  }

  public int relayBatch() {
    List<MqOutboxRecord> records = store.claimBatch(properties.getBatchSize(),
        properties.getLeaseSeconds());
    for (MqOutboxRecord record : records) {
      try {
        publisher.publish(record);
        store.markSent(record.id());
      } catch (Exception e) {
        int attempts = record.attempts() + 1;
        boolean terminal = attempts >= properties.getMaxAttempts();
        long retryDelay = retryDelaySeconds(record.attempts(),
            properties.getInitialRetrySeconds(), properties.getMaxRetrySeconds());
        store.markFailed(record.id(), attempts, retryDelay, terminal, errorMessage(e));
        if (terminal) {
          log.error("MQ outbox event {} exhausted {} attempts", record.id(), attempts, e);
        } else {
          log.warn("MQ outbox event {} publish failed; retry in {}s", record.id(), retryDelay,
              e);
        }
      }
    }
    return records.size();
  }

  public int purgeExpiredSent() {
    return store.purgeSentBefore(LocalDateTime.now().minusDays(properties.getRetentionDays()));
  }

  static long retryDelaySeconds(int previousAttempts, long initialSeconds, long maxSeconds) {
    int exponent = Math.min(Math.max(previousAttempts, 0), 30);
    long multiplier = 1L << exponent;
    if (initialSeconds > Long.MAX_VALUE / multiplier) {
      return maxSeconds;
    }
    return Math.min(initialSeconds * multiplier, maxSeconds);
  }

  private static String errorMessage(Exception exception) {
    String message = exception.getMessage();
    if (message == null || message.isBlank()) {
      message = exception.getClass().getName();
    }
    return message.length() <= MAX_ERROR_LENGTH ? message : message.substring(0, MAX_ERROR_LENGTH);
  }
}
