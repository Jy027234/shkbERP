-- SHKB 工具/设备从属记录 schema。
--
-- V1.13 已建立工具和设备主表；本迁移补齐附件、工具计量记录、
-- 计量记录附件和设备维保记录。只新增缺失表，不修改已有业务数据。

CREATE TABLE IF NOT EXISTS `shkb_tool_file` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `url` varchar(1000) NOT NULL COMMENT '文件地址',
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名称',
  `tool_id` varchar(32) NOT NULL COMMENT '工具ID',
  `create_time` datetime NOT NULL COMMENT '上传时间',
  `content_type` varchar(255) DEFAULT NULL COMMENT 'Content-Type',
  `file_size` varchar(32) DEFAULT NULL COMMENT '可读文件大小',
  `file_suffix` varchar(32) DEFAULT NULL COMMENT '文件后缀',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tool_file_tool_time` (`tool_id`, `create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='工具附件';

CREATE TABLE IF NOT EXISTS `shkb_tool_record` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `tool_id` varchar(32) NOT NULL COMMENT '工具ID',
  `maintenancen_user` varchar(100) NOT NULL COMMENT '计量人员',
  `certificate_number` varchar(100) NOT NULL COMMENT '证书编号',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(32) NOT NULL COMMENT '创建人',
  `create_by_id` varchar(32) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(32) NOT NULL COMMENT '修改人',
  `update_by_id` varchar(32) NOT NULL COMMENT '修改人ID',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `maintenance_time` date NOT NULL COMMENT '计量时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tool_record_tool_time` (`tool_id`, `maintenance_time`, `create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='工具计量记录';

CREATE TABLE IF NOT EXISTS `shkb_tool_record_file` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `url` varchar(1000) NOT NULL COMMENT '文件地址',
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名称',
  `record_id` varchar(32) NOT NULL COMMENT '工具计量记录ID',
  `create_time` datetime NOT NULL COMMENT '上传时间',
  `content_type` varchar(255) DEFAULT NULL COMMENT 'Content-Type',
  `file_size` varchar(32) DEFAULT NULL COMMENT '可读文件大小',
  `file_suffix` varchar(32) DEFAULT NULL COMMENT '文件后缀',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tool_record_file_record_time` (`record_id`, `create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='工具计量记录附件';

CREATE TABLE IF NOT EXISTS `shkb_device_file` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `url` varchar(1000) NOT NULL COMMENT '文件地址',
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名称',
  `device_id` varchar(32) NOT NULL COMMENT '设备ID',
  `create_time` datetime NOT NULL COMMENT '上传时间',
  `content_type` varchar(255) DEFAULT NULL COMMENT 'Content-Type',
  `file_size` varchar(32) DEFAULT NULL COMMENT '可读文件大小',
  `file_suffix` varchar(32) DEFAULT NULL COMMENT '文件后缀',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_device_file_device_time` (`device_id`, `create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='设备附件';

CREATE TABLE IF NOT EXISTS `shkb_device_record` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `device_id` varchar(32) NOT NULL COMMENT '设备ID',
  `maintenancen_user` varchar(100) NOT NULL COMMENT '维保人员',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(32) NOT NULL COMMENT '创建人',
  `create_by_id` varchar(32) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(32) NOT NULL COMMENT '修改人',
  `update_by_id` varchar(32) NOT NULL COMMENT '修改人ID',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `maintenance_time` date DEFAULT NULL COMMENT '维保时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_device_record_device_time` (`device_id`, `maintenance_time`, `create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='设备维保记录';
