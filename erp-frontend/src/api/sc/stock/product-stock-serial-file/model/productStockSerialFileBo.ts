/**
 * 商品序列号库存附件信息
 */
export interface ProductStockSerialFileBo {
  /**
   * ID
   */
  id: string;

  /**
   * 序列号库存ID
   */
  stockSerialId: string;

  /**
   * 文件名
   */
  fileName: string;

  /**
   * 文件大小（字节）
   */
  fileSize: string;

  /**
   * 文件URL
   */
  url: string;

  /**
   * 内容类型
   */
  contentType: string;

  /**
   * 文件后缀
   */
  fileSuffix: string;

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
