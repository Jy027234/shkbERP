package com.lframework.xingyun.sc.service.stock.transfer;

import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.sc.entity.ScTransferOrderDetailSerial;

public interface ScTransferOrderDetailSerialService extends BaseMpService<ScTransferOrderDetailSerial> {

    /**
     * 仅在在途状态时原子置为已收货。
     *
     * @return 更新行数
     */
    int receiveSerial(String id);
}
