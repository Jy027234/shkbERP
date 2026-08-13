package com.lframework.xingyun.sc.mappers;

import com.lframework.starter.web.core.mapper.BaseMapper;
import com.lframework.xingyun.sc.bo.stock.serial.QueryProductStockSerialBo;
import com.lframework.xingyun.sc.entity.ProductStockSerial;
import com.lframework.xingyun.sc.vo.stock.serial.QueryProductStockSerialVo;
import org.apache.ibatis.annotations.Mapper;

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
}
