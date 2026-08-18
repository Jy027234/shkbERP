package com.lframework.xingyun.sc.impl.stock.transfer;

import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.xingyun.sc.entity.ScTransferOrderDetailSerial;
import com.lframework.xingyun.sc.mappers.ScTransferOrderDetailSerialMapper;
import com.lframework.xingyun.sc.service.stock.transfer.ScTransferOrderDetailSerialService;
import org.springframework.stereotype.Service;

@Service
public class ScTransferOrderDetailSerialServiceImpl extends
    BaseMpServiceImpl<ScTransferOrderDetailSerialMapper, ScTransferOrderDetailSerial>
    implements ScTransferOrderDetailSerialService {

  @Override
  public int receiveSerial(String id) {
    return getBaseMapper().receiveSerial(id);
  }
}
