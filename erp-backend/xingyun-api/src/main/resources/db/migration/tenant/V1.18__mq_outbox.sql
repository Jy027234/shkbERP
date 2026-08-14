CREATE TABLE IF NOT EXISTS `sys_mq_outbox` (
  `id` varchar(32) NOT NULL COMMENT '事件ID',
  `event_type` varchar(64) NOT NULL COMMENT '事件类型',
  `payload` longtext NOT NULL COMMENT '消息JSON',
  `tenant_id` int DEFAULT NULL COMMENT '兼容租户上下文',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '0待发送 1发送中 2已发送 3永久失败',
  `attempts` int NOT NULL DEFAULT 0 COMMENT '已失败次数',
  `next_attempt_time` datetime(6) DEFAULT NULL COMMENT '下次发送时间',
  `locked_until` datetime(6) DEFAULT NULL COMMENT '发送租约截止时间',
  `last_error` varchar(1000) DEFAULT NULL COMMENT '最近错误',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `sent_time` datetime(6) DEFAULT NULL COMMENT '确认发送时间',
  PRIMARY KEY (`id`),
  KEY `idx_mq_outbox_dispatch` (`status`, `next_attempt_time`, `locked_until`, `create_time`),
  KEY `idx_mq_outbox_sent` (`status`, `sent_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事务消息发件箱';

CREATE TABLE IF NOT EXISTS `sys_mq_inbox` (
  `event_id` varchar(32) NOT NULL COMMENT '事件ID',
  `consumer_name` varchar(100) NOT NULL COMMENT '消费者',
  `processed_time` datetime(6) NOT NULL COMMENT '处理时间',
  PRIMARY KEY (`event_id`, `consumer_name`),
  KEY `idx_mq_inbox_processed` (`processed_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息消费去重记录';
