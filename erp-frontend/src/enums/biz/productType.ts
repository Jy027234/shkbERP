import { BaseEnum, BaseEnumItem } from '@/enums/baseEnum';

const PRODUCT_TYPE: BaseEnum<number, string> = new BaseEnum<number, string>();
PRODUCT_TYPE.set('NORMAL', new BaseEnumItem<number, string>(1, '普通航材'));
PRODUCT_TYPE.set('BUNDLE', new BaseEnumItem<number, string>(2, '组合航材'));

export { PRODUCT_TYPE };
