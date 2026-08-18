package com.lframework.xingyun.sc.impl.stock.take;

import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.xingyun.sc.entity.TakeStockSheetDetailBatch;
import com.lframework.xingyun.sc.mappers.TakeStockSheetDetailBatchMapper;
import com.lframework.xingyun.sc.service.stock.take.TakeStockSheetDetailBatchService;
import org.springframework.stereotype.Service;

@Service
public class TakeStockSheetDetailBatchServiceImpl extends
    BaseMpServiceImpl<TakeStockSheetDetailBatchMapper, TakeStockSheetDetailBatch>
    implements TakeStockSheetDetailBatchService {

}
