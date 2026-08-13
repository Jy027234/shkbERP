import { TakeStockSheetProductVo } from '@/api/sc/stock/take/sheet/model/takeStockSheetProductVo';

export interface UpdateTakeStockSheetVo {
  /**
   * ID
   */
  id: string;

  /**
   * 备注
   */
  description: string;

  /**
   * 航材信息
   */
  products: TakeStockSheetProductVo[];
}
