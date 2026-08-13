package com.lframework.xingyun.sc.impl.stock;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.FileUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.UploadUtil;
import com.lframework.xingyun.sc.entity.ProductStockBatchFile;
import com.lframework.xingyun.sc.service.stock.ProductStockBatchFileService;
import com.lframework.xingyun.sc.mappers.ProductStockBatchFileMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
* @author kison
* @description 针对表【tbl_product_stock_batch_file(商品批次证书)】的数据库操作Service实现
* @createDate 2025-08-04 10:49:35
*/
@Service
public class ProductStockBatchFileServiceImpl extends BaseMpServiceImpl<ProductStockBatchFileMapper, ProductStockBatchFile>
    implements ProductStockBatchFileService{
    
    /**
     * 上传批次库存附件
     *
     * @param batchId 批次库存ID
     * @param files   文件列表
     * @return 文件ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> uploadBatchFiles(String batchId, List<MultipartFile> files) {
        if (StringUtil.isBlank(batchId)) {
            throw new DefaultClientException("批次库存ID不能为空！");
        }
        
        List<String> ids = new ArrayList<>();
        
        if (CollectionUtil.isEmpty(files)) {
            throw new DefaultClientException("请选择上传的文件！");
        }
        
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            
            // 上传文件并获取URL
            String url = UploadUtil.upload(file).getUrl();
            
            // 创建附件记录
            ProductStockBatchFile batchFile = new ProductStockBatchFile();
            batchFile.setId(IdUtil.getId());
            batchFile.setStockBatchId(batchId);
            batchFile.setUrl(url);
            
            // 获取文件后缀
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                int lastDotIndex = originalFilename.lastIndexOf('.');
                if (lastDotIndex > 0) {
                    batchFile.setFileSuffix(originalFilename.substring(lastDotIndex + 1));
                }
            }
            
            // 设置文件大小
            batchFile.setFileSize(FileUtil.readableFileSize(file.getSize()));
            
            // 设置文件名称
            batchFile.setFileName(file.getOriginalFilename());
            
            // 设置ContentType
            batchFile.setContentType(file.getContentType());
            
            // 设置创建时间
            batchFile.setCreateTime(new Date());
            
            this.save(batchFile);
            ids.add(batchFile.getId());
        }
        
        return ids;
    }
    
    /**
     * 获取批次库存附件列表
     *
     * @param batchId 批次库存ID
     * @return 附件列表
     */
    @Override
    public List<ProductStockBatchFile> getBatchFiles(String batchId) {
        if (StringUtil.isBlank(batchId)) {
            throw new DefaultClientException("批次库存ID不能为空！");
        }
        
        LambdaQueryWrapper<ProductStockBatchFile> queryWrapper = Wrappers.lambdaQuery(ProductStockBatchFile.class)
                .eq(ProductStockBatchFile::getStockBatchId, batchId)
                .orderByDesc(ProductStockBatchFile::getCreateTime);
        
        return this.list(queryWrapper);
    }
    
    /**
     * 删除批次库存附件
     *
     * @param id 附件ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatchFile(String id) {
        ProductStockBatchFile batchFile = this.getById(id);
        if (batchFile == null) {
            throw new DefaultClientException("附件不存在！");
        }
        
        // 删除数据库记录
        return this.removeById(id);
    }
    
    /**
     * 批量删除批次库存附件
     *
     * @param ids 附件ID列表
     * @return 删除数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteBatchFiles(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return 0;
        }
        
        // 查询要删除的附件信息
        List<ProductStockBatchFile> batchFiles = this.listByIds(ids);
        
        // 删除数据库记录
        this.removeByIds(ids);
        
        return batchFiles.size();
    }
}
