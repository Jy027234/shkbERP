package com.lframework.xingyun.core.queue.outbox;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lframework.xingyun.core.dto.stock.ProductStockChangeDto;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class JdbcMqOutboxStoreTest {

  @Test
  void refusesOutboxWriteOutsideBusinessTransaction() {
    JdbcMqOutboxStore store = new JdbcMqOutboxStore(mock(JdbcTemplate.class),
        new ObjectMapper());

    assertThrows(IllegalStateException.class,
        () -> store.append(MqOutboxEventType.ADD_STOCK, new ProductStockChangeDto()));
  }
}
