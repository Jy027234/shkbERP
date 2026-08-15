export interface ReturnProductVo {
  /**
   * 航材ID
   */
  productId: string;

  /**
   * 采购价
   */
  purchasePrice: number;

  /**
   * 退货数量
   */
  returnNum: number;

  /**
   * 备注
   */
  description: string;

  /**
   * 收货单明细ID
   */
  receiveSheetDetailId: string;

  /**
   * 本次退货序列号，多个序列号用逗号分隔
   */
  serialNumberList: string;
}
