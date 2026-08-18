package com.lframework.xingyun.sc.service.stock.transfer;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.sc.entity.ScTransferOrderDetailBatch;

public interface ScTransferOrderDetailBatchService extends BaseMpService<ScTransferOrderDetailBatch> {

    /**
     * 仅在未收数量充足时原子累计收货。
     *
     * @return 更新行数
     */
    int receiveBatch(String id, Integer receiveNum);
}
