import { BaseEnum, BaseEnumItem } from '@/enums/baseEnum';

/**
 * 单位代码枚举
 */
const UNIT_CODE: BaseEnum<string, string> = new BaseEnum<string, string>();
UNIT_CODE.set('SHKB', new BaseEnumItem<string, string>('SHKB', 'SHKB'));
UNIT_CODE.set('BMBJ', new BaseEnumItem<string, string>('BMBJ', 'BMBJ'));
UNIT_CODE.set('BMZD', new BaseEnumItem<string, string>('BMZD', 'BMZD'));
UNIT_CODE.set('SHZD', new BaseEnumItem<string, string>('SHZD', 'SHZD'));

export { UNIT_CODE };
