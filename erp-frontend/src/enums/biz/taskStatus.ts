import { BaseEnum, BaseEnumItem } from '@/enums/baseEnum';

/**
 * 任务状态枚举
 */
const TASK_STATUS: BaseEnum<string, string> = new BaseEnum<string, string>();
TASK_STATUS.set('WAIT_EVALUATION', new BaseEnumItem<string, string>('WAIT_EVALUATION', '待技术评估'));
TASK_STATUS.set('WAIT_DISPATCH', new BaseEnumItem<string, string>('WAIT_DISPATCH', '待派发'));
TASK_STATUS.set('EXECUTION', new BaseEnumItem<string, string>('EXECUTION', '维修执行'));
TASK_STATUS.set('RETURNED', new BaseEnumItem<string, string>('RETURNED', '任务退修'));
TASK_STATUS.set('CLOSED', new BaseEnumItem<string, string>('CLOSED', '任务关闭'));

export { TASK_STATUS };
