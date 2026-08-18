package com.lframework.xingyun.sc.impl.stock;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.utils.Assert;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.service.product.ProductService;
import com.lframework.xingyun.sc.entity.ProductStockBatch;
import com.lframework.xingyun.sc.mappers.ProductStockBatchMapper;
import com.lframework.xingyun.sc.service.stock.ProductStockBatchService;
import com.lframework.xingyun.sc.vo.stock.batch.QueryProductStockBatchVo;
import com.lframework.xingyun.sc.vo.stock.batch.UpdateProductStockBatchVo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author kison
* @description 针对表【tbl_product_stock_batch(商品库存批次)】的数据库操作Service实现
* @createDate 2025-08-04 10:49:34
*/
@Service
public class ProductStockBatchServiceImpl extends BaseMpServiceImpl<ProductStockBatchMapper, ProductStockBatch>
    implements ProductStockBatchService {

    // 其他需要的Service可以在这里注入

    @Override
    public int subStock(String id, String productId, String scId, Integer stockNum) {
        return getBaseMapper().subStock(id, productId, scId, stockNum);
    }

    @Override
    public int addStock(String id, String productId, String scId, Integer stockNum) {
        return getBaseMapper().addStock(id, productId, scId, stockNum);
    }

    @Override
    public PageResult<ProductStockBatch> query(Integer pageIndex, Integer pageSize, QueryProductStockBatchVo vo) {
        Assert.greaterThanZero(pageIndex);
        Assert.greaterThanZero(pageSize);

        PageHelperUtil.startPage(pageIndex, pageSize);
        List<ProductStockBatch> datas = getBaseMapper().query(vo);

        return PageResultUtil.convert(new PageInfo<>(datas));
    }

    @Override
    public ProductStockBatch findById(String id) {
        return getById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateInfo(UpdateProductStockBatchVo vo) {
        ProductStockBatch batch = getById(vo.getId());
        if (batch == null) {
            throw new IllegalArgumentException("批次库存不存在！");
        }

        LambdaUpdateWrapper<ProductStockBatch> updateWrapper = Wrappers.lambdaUpdate(ProductStockBatch.class)
                .set(ProductStockBatch::getProductionDate, vo.getProductionDate())
                .set(ProductStockBatch::getExpiryDate, vo.getExpiryDate())
                .set(ProductStockBatch::getShelfLocation, vo.getShelfLocation())
                .set(ProductStockBatch::getSupplierId, vo.getSupplierId())
                .eq(ProductStockBatch::getId, vo.getId());

        getBaseMapper().update(null, updateWrapper);
    }
}
