package com.lframework.xingyun.core.queue.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lframework.starter.web.core.components.tenant.TenantContextHolder;
import com.lframework.starter.web.core.utils.IdUtil;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class JdbcMqOutboxStore implements MqOutboxWriter {

  static final int STATUS_PENDING = 0;
  static final int STATUS_PROCESSING = 1;
  static final int STATUS_SENT = 2;
  static final int STATUS_FAILED = 3;

  private final JdbcTemplate jdbcTemplate;
  private final ObjectMapper objectMapper;

  public JdbcMqOutboxStore(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
    this.jdbcTemplate = jdbcTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public String append(MqOutboxEventType eventType, Serializable payload) {
    if (!TransactionSynchronizationManager.isActualTransactionActive()) {
      throw new IllegalStateException("MQ outbox writes require an active business transaction");
    }
    String eventId = IdUtil.getId();
    eventType.assignEventId(payload, eventId);
    try {
      jdbcTemplate.update("""
              INSERT INTO sys_mq_outbox
                (id, event_type, payload, tenant_id, status, attempts,
                 next_attempt_time, create_time)
              VALUES (?, ?, ?, ?, ?, 0, NOW(6), NOW(6))
              """,
          eventId, eventType.name(), objectMapper.writeValueAsString(payload),
          TenantContextHolder.getTenantId(), STATUS_PENDING);
      return eventId;
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Cannot serialize MQ outbox payload", e);
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public List<MqOutboxRecord> claimBatch(int batchSize, int leaseSeconds) {
    List<MqOutboxRecord> records = jdbcTemplate.query("""
            SELECT id, event_type, payload, tenant_id, attempts, create_time
            FROM sys_mq_outbox
            WHERE (status = ? AND next_attempt_time <= NOW(6))
               OR (status = ? AND locked_until <= NOW(6))
            ORDER BY create_time, id
            LIMIT ?
            FOR UPDATE SKIP LOCKED
            """,
        (rs, rowNum) -> new MqOutboxRecord(
            rs.getString("id"),
            MqOutboxEventType.valueOf(rs.getString("event_type")),
            rs.getString("payload"),
            rs.getObject("tenant_id", Integer.class),
            rs.getInt("attempts"),
            rs.getTimestamp("create_time").toLocalDateTime()),
        STATUS_PENDING, STATUS_PROCESSING, batchSize);
    LocalDateTime lockedUntil = LocalDateTime.now().plusSeconds(leaseSeconds);
    for (MqOutboxRecord record : records) {
      jdbcTemplate.update("""
              UPDATE sys_mq_outbox
              SET status = ?, locked_until = ?
              WHERE id = ?
              """, STATUS_PROCESSING, Timestamp.valueOf(lockedUntil), record.id());
    }
    return records;
  }

  public void markSent(String id) {
    jdbcTemplate.update("""
            UPDATE sys_mq_outbox
            SET status = ?, sent_time = NOW(6), locked_until = NULL, last_error = NULL
            WHERE id = ? AND status = ?
            """, STATUS_SENT, id, STATUS_PROCESSING);
  }

  public void markFailed(String id, int attempts, long retryDelaySeconds, boolean terminal,
      String error) {
    LocalDateTime nextAttempt = terminal ? null : LocalDateTime.now().plusSeconds(
        retryDelaySeconds);
    jdbcTemplate.update("""
            UPDATE sys_mq_outbox
            SET status = ?, attempts = ?, next_attempt_time = ?, locked_until = NULL,
                last_error = ?
            WHERE id = ? AND status = ?
            """, terminal ? STATUS_FAILED : STATUS_PENDING, attempts,
        nextAttempt == null ? null : Timestamp.valueOf(nextAttempt), error, id, STATUS_PROCESSING);
  }

  public int purgeSentBefore(LocalDateTime cutoff) {
    return jdbcTemplate.update(
        "DELETE FROM sys_mq_outbox WHERE status = ? AND sent_time < ?",
        STATUS_SENT, Timestamp.valueOf(cutoff));
  }
}
