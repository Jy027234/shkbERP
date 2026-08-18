package com.lframework.xingyun.sc.impl.stock;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.Assert;

import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.sc.bo.stock.serial.QueryProductStockSerialBo;
import com.lframework.xingyun.sc.entity.ProductStockSerial;
import com.lframework.xingyun.sc.mappers.ProductStockSerialMapper;
import com.lframework.xingyun.sc.service.stock.ProductStockSerialService;
import com.lframework.xingyun.sc.vo.stock.serial.QueryProductStockSerialVo;
import com.lframework.xingyun.sc.vo.stock.serial.UpdateProductStockSerialVo;
import com.lframework.xingyun.sc.vo.stock.serial.UpdateProductStockSerialNumberVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author kison
* @description 针对表【tbl_product_stock_serial(商品唯一码表)】的数据库操作Service实现
* @createDate 2025-08-04 10:49:35
*/
@Service
public class ProductStockSerialServiceImpl extends BaseMpServiceImpl<ProductStockSerialMapper, ProductStockSerial>
    implements ProductStockSerialService {

    @Override
    public PageResult<QueryProductStockSerialBo> query(Integer pageIndex, Integer pageSize, QueryProductStockSerialVo vo) {
        Assert.greaterThanZero(pageIndex);
        Assert.greaterThanZero(pageSize);

        PageHelperUtil.startPage(pageIndex, pageSize);
        List<QueryProductStockSerialBo> datas = getBaseMapper().query(vo);

        return PageResultUtil.convert(new PageInfo<>(datas));
    }

    @Override
    public ProductStockSerial findById(String id) {
        return getBaseMapper().selectById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateInfo(UpdateProductStockSerialVo vo) {
        ProductStockSerial data = getBaseMapper().selectById(vo.getId());
        if (data == null) {
            throw new DefaultClientException("序列号库存不存在！");
        }

        LambdaUpdateWrapper<ProductStockSerial> updateWrapper = Wrappers.lambdaUpdate(ProductStockSerial.class)
                .set(ProductStockSerial::getProductionDate, vo.getProductionDate())
                .set(ProductStockSerial::getExpiryDate, vo.getExpiryDate())
                .set(ProductStockSerial::getShelfLocation, vo.getShelfLocation())
                .set(ProductStockSerial::getSupplierId, vo.getSupplierId())
                .eq(ProductStockSerial::getId, vo.getId());

        getBaseMapper().update(updateWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateSerialNumber(UpdateProductStockSerialNumberVo vo) {
        ProductStockSerial data = getBaseMapper().selectById(vo.getId());
        if (data == null) {
            throw new DefaultClientException("序列号库存不存在！");
        }

        // 检查新序列号是否已存在
        LambdaUpdateWrapper<ProductStockSerial> checkWrapper = Wrappers.lambdaUpdate(ProductStockSerial.class)
                .eq(ProductStockSerial::getSerialNumber, vo.getSerialNumber())
                .ne(ProductStockSerial::getId, vo.getId());

        Long count = getBaseMapper().selectCount(checkWrapper);
        if (count != null && count > 0) {
            throw new DefaultClientException("序列号已存在！");
        }

        // 更新序列号
        LambdaUpdateWrapper<ProductStockSerial> updateWrapper = Wrappers.lambdaUpdate(ProductStockSerial.class)
                .set(ProductStockSerial::getSerialNumber, vo.getSerialNumber())
                .eq(ProductStockSerial::getId, vo.getId());

        getBaseMapper().update(updateWrapper);
    }

    @Override
    public int updateStatus(String id, Integer fromStatus, Integer toStatus) {
        return getBaseMapper().updateStatus(id, fromStatus, toStatus);
    }

    @Override
    public int receiveTransfer(String id, String batchId) {
        return getBaseMapper().receiveTransfer(id, batchId);
    }
}
