import { BaseEnum, BaseEnumItem } from '@/enums/baseEnum';

/**
 * 合同类型枚举
 */
const CONTRACT_TYPE: BaseEnum<number, string> = new BaseEnum<number, string>();
CONTRACT_TYPE.set('AVIATION', new BaseEnumItem<number, string>(1, '民航'));
CONTRACT_TYPE.set('FACTORY_WB', new BaseEnumItem<number, string>(2, '工厂WB'));
CONTRACT_TYPE.set('FACTORY_L', new BaseEnumItem<number, string>(3, '工厂L'));

export { CONTRACT_TYPE };
