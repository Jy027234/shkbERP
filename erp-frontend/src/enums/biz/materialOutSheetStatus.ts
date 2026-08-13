import { BaseEnum, BaseEnumItem } from '@/enums/baseEnum';

/**
 * 发料出库单状态
 */
const MATERIAL_OUT_SHEET_STATUS: BaseEnum<number, string> = new BaseEnum<number, string>();
// 0：备料中
MATERIAL_OUT_SHEET_STATUS.set('PREPARING', new BaseEnumItem<number, string>(0, '备料中'));
// 2：可领料
MATERIAL_OUT_SHEET_STATUS.set('PICKABLE', new BaseEnumItem<number, string>(2, '可领料'));
// 1：已发料
MATERIAL_OUT_SHEET_STATUS.set('ISSUED', new BaseEnumItem<number, string>(1, '已发料'));

export { MATERIAL_OUT_SHEET_STATUS };
