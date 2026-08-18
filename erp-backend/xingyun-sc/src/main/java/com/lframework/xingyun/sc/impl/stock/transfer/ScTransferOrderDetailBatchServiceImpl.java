package com.lframework.xingyun.sc.impl.stock.transfer;

import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.xingyun.sc.entity.ScTransferOrderDetailBatch;
import com.lframework.xingyun.sc.mappers.ScTransferOrderDetailBatchMapper;
import com.lframework.xingyun.sc.service.stock.transfer.ScTransferOrderDetailBatchService;
import org.springframework.stereotype.Service;

@Service
public class ScTransferOrderDetailBatchServiceImpl extends
    BaseMpServiceImpl<ScTransferOrderDetailBatchMapper, ScTransferOrderDetailBatch>
    implements ScTransferOrderDetailBatchService {

  @Override
  public int receiveBatch(String id, Integer receiveNum) {
    return getBaseMapper().receiveBatch(id, receiveNum);
  }
}
