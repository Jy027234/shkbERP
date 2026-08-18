package com.lframework.xingyun.sc.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.sc.entity.ScTransferOrderDetailBatch;
import org.apache.ibatis.annotations.Param;

/**
 * 仓库调拨单批次明细 Mapper 接口
 */
public interface ScTransferOrderDetailBatchMapper extends BaseMapper<ScTransferOrderDetailBatch> {

    /**
     * 仅在未收数量充足时原子累计收货。
     *
     * @return 更新行数
     */
    int receiveBatch(@Param("id") String id, @Param("receiveNum") Integer receiveNum);
}
