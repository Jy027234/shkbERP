package com.lframework.xingyun.chart.listeners.mq;

import com.lframework.xingyun.chart.enums.OrderChartBizType;
import com.lframework.xingyun.chart.service.OrderChartService;
import com.lframework.xingyun.chart.vo.CreateOrderChartVo;
import com.lframework.xingyun.core.dto.order.ApprovePassOrderDto;
import com.lframework.xingyun.core.queue.MqStringPool;
import com.lframework.xingyun.core.queue.outbox.MqInboxDeduplicator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderDataToChartListener {

  @Autowired
  private OrderChartService orderChartService;

  @Autowired
  private MqInboxDeduplicator inboxDeduplicator;

  @Transactional(rollbackFor = Exception.class)
  @RabbitListener(bindings = {
      @QueueBinding(value = @Queue(value = "chart.approve_pass_order"), exchange = @Exchange(value = MqStringPool.APPROVE_PASS_ORDER_EXCHANGE, type = ExchangeTypes.FANOUT))})
  public void execute(Message<ApprovePassOrderDto> message) {
    ApprovePassOrderDto event = message.getPayload();
    if (!inboxDeduplicator.accept(event.getEventId(), "chart.approve_pass_order")) {
      log.info("忽略重复订单审批事件 eventId={}", event.getEventId());
      return;
    }
    OrderChartBizType bizType = this.convertBizType(event.getOrderType());
    if (bizType == null) {
      log.error("orderType={}，无法匹配业务类型", event.getOrderType());
      return;
    }

    CreateOrderChartVo vo = new CreateOrderChartVo();
    vo.setTotalAmount(event.getTotalAmount());
    vo.setCreateTime(event.getApproveTime());
    vo.setBizType(bizType.getCode());

    orderChartService.create(vo);
  }

  private OrderChartBizType convertBizType(ApprovePassOrderDto.OrderType orderType) {

    if (orderType == ApprovePassOrderDto.OrderType.PURCHASE_ORDER) {
      return OrderChartBizType.PURCHASE_ORDER;
    }
    if (orderType == ApprovePassOrderDto.OrderType.PURCHASE_RETURN) {
      return OrderChartBizType.PURCHASE_RETURN;
    }
    if (orderType == ApprovePassOrderDto.OrderType.SALE_ORDER) {
      return OrderChartBizType.SALE_ORDER;
    }
    if (orderType == ApprovePassOrderDto.OrderType.SALE_RETURN) {
      return OrderChartBizType.SALE_RETURN;
    }
    if (orderType == ApprovePassOrderDto.OrderType.RETAIL_OUT_SHEET) {
      return OrderChartBizType.RETAIL_OUT_SHEET;
    }
    if (orderType == ApprovePassOrderDto.OrderType.RETAIL_RETURN) {
      return OrderChartBizType.RETAIL_RETURN;
    }

    return null;
  }
}
