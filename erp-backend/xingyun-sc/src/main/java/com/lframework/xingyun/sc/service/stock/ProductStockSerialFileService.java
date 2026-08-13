package com.lframework.xingyun.sc.service.stock;

import com.lframework.xingyun.sc.entity.ProductStockSerialFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author kison
* @description 针对表【tbl_product_stock_serial_file(商品序列号库存附件)】的数据库操作Service
* @createDate 2025-08-04 10:49:35
*/
public interface ProductStockSerialFileService extends IService<ProductStockSerialFile> {

    /**
     * 上传序列号库存附件
     * @param serialId 序列号库存ID
     * @param files 文件列表
     * @return 上传成功的文件名列表
     */
    List<String> uploadSerialFiles(String serialId, MultipartFile[] files);

    /**
     * 根据序列号库存ID查询附件列表
     * @param serialId 序列号库存ID
     * @return 附件列表
     */
    List<ProductStockSerialFile> getBySerialId(String serialId);

    /**
     * 删除序列号库存附件
     * @param id 附件ID
     */
    void deleteSerialFile(String id);

    /**
     * 批量删除序列号库存附件
     * @param ids 附件ID列表
     * @return 删除的数量
     */
    Integer batchDeleteSerialFiles(List<String> ids);
}
