package com.lframework.xingyun.sc.listeners.app;

import com.lframework.xingyun.core.queue.outbox.MqOutboxEventType;
import com.lframework.xingyun.core.queue.outbox.MqOutboxWriter;
import com.lframework.xingyun.sc.events.stock.AddStockEvent;
import com.lframework.xingyun.sc.events.stock.SubStockEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class StockChangeToMqListener {

  private final MqOutboxWriter outboxWriter;

  public StockChangeToMqListener(MqOutboxWriter outboxWriter) {
    this.outboxWriter = outboxWriter;
  }

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void addStock(AddStockEvent addStockEvent) {
    outboxWriter.append(MqOutboxEventType.ADD_STOCK, addStockEvent.getChange());
  }

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void subStock(SubStockEvent subStockEvent) {
    outboxWriter.append(MqOutboxEventType.SUB_STOCK, subStockEvent.getChange());
  }
}
