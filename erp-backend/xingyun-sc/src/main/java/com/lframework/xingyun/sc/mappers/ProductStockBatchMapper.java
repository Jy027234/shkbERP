package com.lframework.xingyun.sc.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.sc.entity.ProductStockBatch;
import com.lframework.xingyun.sc.vo.stock.batch.QueryProductStockBatchVo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author kison
* @description 针对表【tbl_product_stock_batch(商品库存批次)】的数据库操作Mapper
* @Entity com.lframework.xingyun.sc.entity.ProductStockBatch
*/
@Mapper
public interface ProductStockBatchMapper extends BaseMapper<ProductStockBatch> {

    /**
     * 仅在批次库存充足时原子扣减。
     */
    int subStock(@Param("id") String id, @Param("productId") String productId,
        @Param("scId") String scId, @Param("stockNum") Integer stockNum);

    /**
     * 查询商品批次库存信息
     * 
     * @param vo 查询参数
     * @return 列表
     */
    List<ProductStockBatch> query(QueryProductStockBatchVo vo);
}
