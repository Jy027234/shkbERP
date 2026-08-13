package com.lframework.xingyun.sc.service.stock;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.service.BaseMpService;
import com.lframework.xingyun.core.dto.stock.ProductStockChangeDto;
import com.lframework.xingyun.sc.entity.ProductStock;
import com.lframework.xingyun.sc.vo.stock.AddProductStockVo;
import com.lframework.xingyun.sc.vo.stock.QueryProductStockVo;
import com.lframework.xingyun.sc.vo.stock.SubProductStockVo;
import com.lframework.xingyun.sc.vo.stock.SubProductStockBatchVo;
import java.util.List;

public interface ProductStockService extends BaseMpService<ProductStock> {

  /**
   * 查询列表
   *
   * @param pageIndex
   * @param pageSize
   * @param vo
   * @return
   */
  PageResult<ProductStock> query(Integer pageIndex, Integer pageSize, QueryProductStockVo vo);

  /**
   * 查询列表
   *
   * @param vo
   * @return
   */
  List<ProductStock> query(QueryProductStockVo vo);

  /**
   * 查询库存数量小于等于10且分类名称为“航材”的商品库存（按商品汇总）
   */
  List<ProductStock> queryLowInventoryAviation();

  /**
   * 根据商品ID、仓库ID查询
   *
   * @param productId
   * @param scId
   * @return
   */
  ProductStock getByProductIdAndScId(String productId, String scId);

  /**
   * 根据商品ID、仓库ID查询
   *
   * @param productIds
   * @param scId
   * @return
   */
  List<ProductStock> getByProductIdsAndScId(List<String> productIds, String scId,
      Integer productType);

  /**
   * 入库
   *
   * @param vo
   */
  ProductStockChangeDto addStock(AddProductStockVo vo);

  /**
   * 出库
   *
   * @param vo
   */
  ProductStockChangeDto subStock(SubProductStockVo vo);

  /**
   * 批次库存出库
   *
   * @param vo
   */
  ProductStockChangeDto subStockBatch(SubProductStockBatchVo vo);

  /**
   * 出库（不记录流水日志）
   * 用于批次出库场景，避免重复记录流水
   *
   * @param vo
   */
  ProductStockChangeDto subStockWithoutLog(SubProductStockVo vo);
}
