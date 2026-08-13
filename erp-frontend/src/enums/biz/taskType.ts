import { BaseEnum, BaseEnumItem } from '@/enums/baseEnum';

/**
 * 任务类型枚举
 */
const TASK_TYPE: BaseEnum<string, string> = new BaseEnum<string, string>();
TASK_TYPE.set('AVIATION', new BaseEnumItem<string, string>('AVIATION', '民航维修任务'));
TASK_TYPE.set('RECEIVE_WB', new BaseEnumItem<string, string>('RECEIVE_WB', '返厂WB任务'));
TASK_TYPE.set('RECEIVE_L', new BaseEnumItem<string, string>('RECEIVE_L', '返厂L任务'));

export { TASK_TYPE };
