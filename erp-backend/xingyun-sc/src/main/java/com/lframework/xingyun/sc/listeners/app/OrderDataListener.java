package com.lframework.xingyun.sc.listeners.app;

import com.lframework.xingyun.core.queue.outbox.MqOutboxEventType;
import com.lframework.xingyun.core.queue.outbox.MqOutboxWriter;
import com.lframework.xingyun.sc.events.order.ApprovePassOrderEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderDataListener {

  private final MqOutboxWriter outboxWriter;

  public OrderDataListener(MqOutboxWriter outboxWriter) {
    this.outboxWriter = outboxWriter;
  }

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void execute(ApprovePassOrderEvent event) {
    outboxWriter.append(MqOutboxEventType.APPROVE_PASS_ORDER, event.getOrder());
  }
}
