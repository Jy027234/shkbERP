package com.lframework.xingyun.sc.listeners.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.lframework.xingyun.core.dto.order.ApprovePassOrderDto;
import com.lframework.xingyun.core.dto.stock.ProductStockChangeDto;
import com.lframework.xingyun.core.queue.outbox.MqOutboxEventType;
import com.lframework.xingyun.core.queue.outbox.MqOutboxWriter;
import com.lframework.xingyun.sc.events.order.ApprovePassOrderEvent;
import com.lframework.xingyun.sc.events.stock.AddStockEvent;
import com.lframework.xingyun.sc.events.stock.SubStockEvent;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

class TransactionalOutboxListenerTest {

  @Test
  void stockEventsAreWrittenToMatchingOutboxTypes() {
    RecordingWriter writer = new RecordingWriter();
    StockChangeToMqListener listener = new StockChangeToMqListener(writer);
    ProductStockChangeDto add = new ProductStockChangeDto();
    ProductStockChangeDto sub = new ProductStockChangeDto();

    listener.addStock(new AddStockEvent(this, add));
    listener.subStock(new SubStockEvent(this, sub));

    assertEquals(List.of(MqOutboxEventType.ADD_STOCK, MqOutboxEventType.SUB_STOCK), writer.types);
    assertSame(add, writer.payloads.get(0));
    assertSame(sub, writer.payloads.get(1));
  }

  @Test
  void approvedOrderIsWrittenToOutbox() {
    RecordingWriter writer = new RecordingWriter();
    OrderDataListener listener = new OrderDataListener(writer);
    ApprovePassOrderDto order = new ApprovePassOrderDto();
    ApprovePassOrderEvent event = new ApprovePassOrderEvent(this, order) { };

    listener.execute(event);

    assertEquals(List.of(MqOutboxEventType.APPROVE_PASS_ORDER), writer.types);
    assertSame(order, writer.payloads.get(0));
  }

  @Test
  void allOutboxListenersRunBeforeBusinessCommit() throws Exception {
    assertBeforeCommit(StockChangeToMqListener.class.getMethod("addStock", AddStockEvent.class));
    assertBeforeCommit(StockChangeToMqListener.class.getMethod("subStock", SubStockEvent.class));
    assertBeforeCommit(OrderDataListener.class.getMethod("execute", ApprovePassOrderEvent.class));
  }

  private static void assertBeforeCommit(Method method) {
    TransactionalEventListener annotation = method.getAnnotation(TransactionalEventListener.class);
    assertEquals(TransactionPhase.BEFORE_COMMIT, annotation.phase());
  }

  private static class RecordingWriter implements MqOutboxWriter {

    private final List<MqOutboxEventType> types = new ArrayList<>();
    private final List<Serializable> payloads = new ArrayList<>();

    @Override
    public String append(MqOutboxEventType eventType, Serializable payload) {
      types.add(eventType);
      payloads.add(payload);
      return "event-" + types.size();
    }
  }
}
