import { BaseEnum, BaseEnumItem } from '@/enums/baseEnum';

/**
 * 合同状态枚举
 */
const CONTRACT_STATUS: BaseEnum<number, string> = new BaseEnum<number, string>();
CONTRACT_STATUS.set('WAIT_CREATE', new BaseEnumItem<number, string>(0, '待生成合同任务'));
CONTRACT_STATUS.set('TASK_EXECUTING', new BaseEnumItem<number, string>(1, '任务执行中'));
CONTRACT_STATUS.set('TASK_RETURN', new BaseEnumItem<number, string>(3, '任务退修'));
CONTRACT_STATUS.set('CONTRACT_CLOSE', new BaseEnumItem<number, string>(2, '合同关闭'));
CONTRACT_STATUS.set('TASK_CLOSE', new BaseEnumItem<number, string>(4, '任务关闭'));
CONTRACT_STATUS.set('WAIT_DELIVERY', new BaseEnumItem<number, string>(5, '待交付'));

export { CONTRACT_STATUS };
