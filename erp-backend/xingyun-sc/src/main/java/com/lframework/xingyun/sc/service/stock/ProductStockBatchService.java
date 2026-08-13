package com.lframework.xingyun.sc.service.stock;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.xingyun.sc.entity.ProductStockBatch;
import com.lframework.xingyun.sc.vo.stock.batch.QueryProductStockBatchVo;
import com.lframework.xingyun.sc.vo.stock.batch.UpdateProductStockBatchVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author kison
* @description 针对表【tbl_product_stock_batch(商品库存批次)】的数据库操作Service
* @createDate 2025-08-04 10:49:35
*/
public interface ProductStockBatchService extends IService<ProductStockBatch> {

    /**
     * 仅在批次、商品、仓库匹配且库存充足时原子扣减。
     *
     * @return 更新行数
     */
    int subStock(String id, String productId, String scId, Integer stockNum);

    /**
     * 查询批次库存
     *
     * @param pageIndex 页码
     * @param pageSize  每页数量
     * @param vo       查询参数
     * @return 查询结果
     */
    PageResult<ProductStockBatch> query(Integer pageIndex, Integer pageSize, QueryProductStockBatchVo vo);

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return 批次库存
     */
    ProductStockBatch findById(String id);
    
    /**
     * 修改批次库存信息
     *
     * @param vo 请求参数
     */
    void updateInfo(UpdateProductStockBatchVo vo);
}
