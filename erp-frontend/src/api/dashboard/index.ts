import { defHttp } from '/@/utils/http/axios';

const baseUrl = '/shkb/dashboard';
const region = 'cloud-api';

/**
 * 获取指定合同类型的维修工单数据
 * @param contractType 合同类型代码（0=民航维修工单，1=返厂WB维修工单，2=返厂L维修工单）
 * @param startDate 开始日期
 * @param endDate 结束日期
 */
export function getMaintenanceTypeData(
  contractType: number,
  startDate: string,
  endDate: string,
): Promise<any> {
  return defHttp.get<any>(
    {
      url: baseUrl + '/maintenance-type-data',
      params: {
        contractType,
        startDate,
        endDate,
      },
    },
    {
      region,
    },
  );
}

/**
 * 获取库存数据
 */
export function getInventoryData(): Promise<any> {
  return defHttp.get<any>(
    {
      url: baseUrl + '/inventory-data',
    },
    {
      region,
    },
  );
}

/**
 * 获取工具设备数据
 */
export function getToolsDeviceData(): Promise<any> {
  return defHttp.get<any>(
    {
      url: baseUrl + '/tools-device-data',
    },
    {
      region,
    },
  );
}
