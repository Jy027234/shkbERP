package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.FileUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.utils.IdUtil;
import com.lframework.starter.web.core.utils.UploadUtil;
import com.lframework.xingyun.shkb.entity.ProductStorageFile;
import com.lframework.xingyun.shkb.mappers.ProductStorageFileMapper;
import com.lframework.xingyun.shkb.service.ProductStorageFileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* 针对表【shkb_product_storage_file(客户接收单附件)】的数据库操作Service实现
*/
@Service
public class ProductStorageFileServiceImpl extends BaseMpServiceImpl<ProductStorageFileMapper, ProductStorageFile>
    implements ProductStorageFileService {

  @Override
  @Transactional(rollbackFor = Exception.class)
  public List<String> uploadProductStorageFiles(String productStorageId, List<MultipartFile> files) {
    List<String> fileIds = new ArrayList<>();
    if (CollectionUtil.isEmpty(files)) {
      return fileIds;
    }

    for (MultipartFile file : files) {
      if (file == null || file.isEmpty()) {
        continue;
      }

      String url = UploadUtil.upload(file).getUrl();

      ProductStorageFile rec = new ProductStorageFile();
      rec.setId(IdUtil.getId());
      rec.setProductStorageId(productStorageId);
      rec.setUrl(url);
      rec.setCreateTime(new Date());
      rec.setFileName(file.getOriginalFilename());
      rec.setContentType(file.getContentType());
      rec.setFileSize(FileUtil.readableFileSize(file.getSize()));

      String originalFilename = file.getOriginalFilename();
      if (originalFilename != null) {
        int idx = originalFilename.lastIndexOf('.');
        if (idx > 0 && idx < originalFilename.length() - 1) {
          rec.setFileSuffix(originalFilename.substring(idx + 1));
        }
      }

      this.save(rec);
      fileIds.add(rec.getId());
    }

    return fileIds;
  }

  @Override
  public List<ProductStorageFile> getProductStorageFiles(String productStorageId) {
    LambdaQueryWrapper<ProductStorageFile> qw = Wrappers.lambdaQuery(ProductStorageFile.class)
        .eq(ProductStorageFile::getProductStorageId, productStorageId)
        .orderByDesc(ProductStorageFile::getCreateTime);
    return this.list(qw);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean deleteProductStorageFile(String id) {
    ProductStorageFile file = this.getById(id);
    if (file == null) {
      return false;
    }
    // 如需删除物理文件，可在此调用 UploadUtil 的删除方法
    // UploadUtil.deleteFile(file.getUrl());
    return this.removeById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int batchDeleteProductStorageFiles(List<String> ids) {
    if (CollectionUtil.isEmpty(ids)) {
      return 0;
    }
    int count = 0;
    for (String id : ids) {
      if (deleteProductStorageFile(id)) {
        count++;
      }
    }
    return count;
  }
}




