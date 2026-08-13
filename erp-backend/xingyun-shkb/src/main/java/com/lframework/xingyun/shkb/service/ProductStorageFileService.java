package com.lframework.xingyun.shkb.service;

import com.lframework.xingyun.shkb.entity.ProductStorageFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* 针对表【shkb_product_storage_file(客户接收单附件)】的数据库操作Service
*/
public interface ProductStorageFileService extends IService<ProductStorageFile> {

  /**
   * 上传客户接收单附件
   * @param productStorageId 成品出入库ID
   * @param files 文件列表
   * @return 文件ID列表
   */
  List<String> uploadProductStorageFiles(String productStorageId, List<MultipartFile> files);

  /**
   * 获取客户接收单附件列表
   * @param productStorageId 成品出入库ID
   * @return 附件列表
   */
  List<ProductStorageFile> getProductStorageFiles(String productStorageId);

  /**
   * 删除客户接收单附件
   * @param id 附件ID
   * @return 是否成功
   */
  boolean deleteProductStorageFile(String id);

  /**
   * 批量删除客户接收单附件
   * @param ids 附件ID列表
   * @return 成功数量
   */
  int batchDeleteProductStorageFiles(List<String> ids);
}
