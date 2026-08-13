package com.lframework.xingyun.sc.service.stock;

import com.lframework.xingyun.sc.entity.ProductStockBatchFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author kison
* @description 针对表【tbl_product_stock_batch_file(商品批次证书)】的数据库操作Service
* @createDate 2025-08-04 10:49:35
*/
public interface ProductStockBatchFileService extends IService<ProductStockBatchFile> {

    /**
     * 上传批次库存附件
     * 
     * @param batchId 批次库存ID
     * @param files 文件列表
     * @return 文件ID列表
     */
    List<String> uploadBatchFiles(String batchId, List<MultipartFile> files);
    
    /**
     * 获取批次库存附件列表
     * 
     * @param batchId 批次库存ID
     * @return 附件列表
     */
    List<ProductStockBatchFile> getBatchFiles(String batchId);
    
    /**
     * 删除批次库存附件
     * 
     * @param id 附件ID
     * @return 是否成功
     */
    boolean deleteBatchFile(String id);
    
    /**
     * 批量删除批次库存附件
     * 
     * @param ids 附件ID列表
     * @return 删除数量
     */
    int batchDeleteBatchFiles(List<String> ids);
}
