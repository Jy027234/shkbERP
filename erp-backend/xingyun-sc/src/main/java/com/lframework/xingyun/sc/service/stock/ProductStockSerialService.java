package com.lframework.xingyun.sc.service.stock;

import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.xingyun.sc.bo.stock.serial.QueryProductStockSerialBo;
import com.lframework.xingyun.sc.entity.ProductStockSerial;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lframework.xingyun.sc.vo.stock.serial.QueryProductStockSerialVo;
import com.lframework.xingyun.sc.vo.stock.serial.UpdateProductStockSerialVo;
import com.lframework.xingyun.sc.vo.stock.serial.UpdateProductStockSerialNumberVo;

/**
* @author kison
* @description 针对表【tbl_product_stock_serial(商品唯一码表)】的数据库操作Service
* @createDate 2025-08-04 10:49:35
*/
public interface ProductStockSerialService extends IService<ProductStockSerial> {

    /**
     * 查询商品序列号库存
     *
     * @param pageIndex
     * @param pageSize
     * @param vo
     * @return
     */
    PageResult<QueryProductStockSerialBo> query(Integer pageIndex, Integer pageSize, QueryProductStockSerialVo vo);

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    ProductStockSerial findById(String id);

    /**
     * 修改商品序列号库存信息
     *
     * @param vo
     */
    void updateInfo(UpdateProductStockSerialVo vo);

    /**
     * 修改商品序列号
     *
     * @param vo
     */
    void updateSerialNumber(UpdateProductStockSerialNumberVo vo);

    /**
     * 仅在当前状态匹配时原子更新序列号状态（用于盘盈/盘亏状态流转）。
     *
     * @return 更新行数
     */
    int updateStatus(String id, Integer fromStatus, Integer toStatus);

    /**
     * 调拨收货：仅当序列号处于出库（在途）状态时原子置为在库并切换到转入仓批次。
     *
     * @return 更新行数
     */
    int receiveTransfer(String id, String batchId);
}
