package com.lframework.xingyun.core.queue.outbox;

import com.lframework.starter.common.utils.StringUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class JdbcMqInboxDeduplicator implements MqInboxDeduplicator {

  private final JdbcTemplate jdbcTemplate;

  public JdbcMqInboxDeduplicator(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public boolean accept(String eventId, String consumerName) {
    if (StringUtil.isBlank(eventId)) {
      return true;
    }
    if (!TransactionSynchronizationManager.isActualTransactionActive()) {
      throw new IllegalStateException("MQ inbox deduplication requires an active transaction");
    }
    return jdbcTemplate.update("""
            INSERT IGNORE INTO sys_mq_inbox (event_id, consumer_name, processed_time)
            VALUES (?, ?, NOW(6))
            """, eventId, consumerName) == 1;
  }
}
