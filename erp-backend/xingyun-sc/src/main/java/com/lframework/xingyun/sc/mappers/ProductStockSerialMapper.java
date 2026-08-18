package com.lframework.xingyun.sc.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.sc.bo.stock.serial.QueryProductStockSerialBo;
import com.lframework.xingyun.sc.entity.ProductStockSerial;
import com.lframework.xingyun.sc.vo.stock.serial.QueryProductStockSerialVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author kison
* @description 针对表【tbl_product_stock_serial(商品唯一码表)】的数据库操作Mapper
* @createDate 2025-08-04 10:49:35
* @Entity com.lframework.xingyun.sc.entity.ProductStockSerial
*/
@Mapper
public interface ProductStockSerialMapper extends BaseMapper<ProductStockSerial> {

    /**
     * 查询商品序列号库存
     *
     * @param vo
     * @return
     */
    List<QueryProductStockSerialBo> query(QueryProductStockSerialVo vo);

    /**
     * 仅在当前状态匹配时原子更新序列号状态（用于盘盈/盘亏状态流转）。
     *
     * @return 更新行数
     */
    int updateStatus(@Param("id") String id, @Param("fromStatus") Integer fromStatus,
        @Param("toStatus") Integer toStatus);

    /**
     * 调拨收货：仅当序列号处于出库（在途）状态时原子置为在库并切换到转入仓批次。
     *
     * @return 更新行数
     */
    int receiveTransfer(@Param("id") String id, @Param("batchId") String batchId);
}
