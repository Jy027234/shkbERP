package com.lframework.xingyun.sc.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.sc.entity.ScTransferOrderDetailSerial;
import org.apache.ibatis.annotations.Param;

/**
 * 仓库调拨单序列号明细 Mapper 接口
 */
public interface ScTransferOrderDetailSerialMapper extends BaseMapper<ScTransferOrderDetailSerial> {

    /**
     * 仅在在途状态时原子置为已收货。
     *
     * @return 更新行数
     */
    int receiveSerial(@Param("id") String id);
}
