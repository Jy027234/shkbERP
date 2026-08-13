/**
 * 商品批次库存附件信息
 */
export interface ProductStockBatchFileBo {
  /**
   * ID
   */
  id: string;

  /**
   * 批次ID
   */
  batchId: string;

  /**
   * 文件名
   */
  fileName: string;

  /**
   * 文件大小（字节）
   */
  fileSize: number;

  /**
   * 文件路径
   */
  filePath: string;

  /**
   * 文件URL
   */
  url: string;

  /**
   * 创建时间
   */
  createTime: string;

  /**
   * 创建人
   */
  createBy: string;

  /**
   * 创建人名称
   */
  createByName: string;
}
