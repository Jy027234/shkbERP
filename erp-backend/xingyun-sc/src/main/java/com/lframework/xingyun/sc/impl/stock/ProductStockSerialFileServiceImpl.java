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
import com.lframework.xingyun.sc.entity.ProductStockSerialFile;
import com.lframework.xingyun.sc.service.stock.ProductStockSerialFileService;
import com.lframework.xingyun.sc.mappers.ProductStockSerialFileMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author kison
* @description 针对表【tbl_product_stock_serial_file(商品序列号库存附件)】的数据库操作Service实现
* @createDate 2025-08-04 10:49:35
*/
@Service
public class ProductStockSerialFileServiceImpl extends BaseMpServiceImpl<ProductStockSerialFileMapper, ProductStockSerialFile>
    implements ProductStockSerialFileService{

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> uploadSerialFiles(String serialId, MultipartFile[] files) {
        List<String> uploadedFileNames = new ArrayList<>();
        
        if (files == null || files.length == 0) {
            return uploadedFileNames;
        }
        
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            
            // 上传文件并获取URL
            String url = UploadUtil.upload(file).getUrl();
            
            // 创建附件记录
            ProductStockSerialFile serialFile = new ProductStockSerialFile();
            serialFile.setId(IdUtil.getId());
            serialFile.setStockSerialId(serialId);
            serialFile.setUrl(url);
            
            // 获取文件后缀
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                int lastDotIndex = originalFilename.lastIndexOf('.');
                if (lastDotIndex > 0) {
                    serialFile.setFileSuffix(originalFilename.substring(lastDotIndex + 1));
                }
            }
            
            // 设置文件大小
            serialFile.setFileSize(FileUtil.readableFileSize(file.getSize()));
            
            // 设置文件名称
            serialFile.setFileName(file.getOriginalFilename());
            
            // 设置ContentType
            serialFile.setContentType(file.getContentType());
            
            // 设置创建时间
            serialFile.setCreateTime(new Date());
            
            this.save(serialFile);
            uploadedFileNames.add(originalFilename);
        }
        
        return uploadedFileNames;
    }

    @Override
    public List<ProductStockSerialFile> getBySerialId(String serialId) {
        if (StringUtil.isBlank(serialId)) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<ProductStockSerialFile> queryWrapper = Wrappers.lambdaQuery(ProductStockSerialFile.class);
        queryWrapper.eq(ProductStockSerialFile::getStockSerialId, serialId)
                   .orderByDesc(ProductStockSerialFile::getCreateTime);
        
        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSerialFile(String id) {
        ProductStockSerialFile serialFile = this.getById(id);
        if (serialFile == null) {
            throw new DefaultClientException("附件不存在！");
        }
        
        // 删除数据库记录
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchDeleteSerialFiles(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return 0;
        }
        
        // 查询要删除的附件信息
        List<ProductStockSerialFile> serialFiles = this.listByIds(ids);
        
        // 删除数据库记录
        this.removeByIds(ids);
        
        return serialFiles.size();
    }
}




