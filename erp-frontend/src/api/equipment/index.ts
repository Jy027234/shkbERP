import { defHttp } from '@/utils/http/axios';

const baseUrl = '/shkb/device';
const recordUrl = '/shkb/device/record';

/**
 * 查询设备列表
 * @param params 查询参数
 * @returns 设备列表
 */
export function query(params: any) {
  return defHttp.get({
    url: `${baseUrl}/query`,
    params,
  });
}

/**
 * 查询设备详情
 * @param id 设备ID
 * @returns 设备详情
 */
export function get(id: string) {
  return defHttp.get({
    url: `${baseUrl}/${id}`,
  });
}

/**
 * 新增设备
 * @param data 设备数据
 * @returns 操作结果
 */
export function create(data: any) {
  return defHttp.post({
    url: baseUrl,
    data,
  });
}

/**
 * 修改设备
 * @param data 设备数据
 * @returns 操作结果
 */
export function update(data: any) {
  return defHttp.put({
    url: baseUrl,
    data,
  });
}

/**
 * 删除设备
 * @param id 设备ID
 * @returns 操作结果
 */
export function deleteById(id: string) {
  return defHttp.delete({
    url: `${baseUrl}/${id}`,
  });
}

/**
 * 批量删除设备
 * @param ids 设备ID数组
 * @returns 操作结果
 */
export function batchDelete(ids: string[]) {
  return defHttp.delete({
    url: `${baseUrl}/batch`,
    params: { ids },
  });
}

/**
 * 查询设备维保记录列表
 * @param params 查询参数
 * @returns 维保记录列表
 */
export function queryDeviceRecords(params: any) {
  return defHttp.get({
    url: `${recordUrl}/query`,
    params,
  });
}

/**
 * 根据ID查询设备维保记录
 * @param id 维保记录ID
 * @returns 维保记录详情
 */
export function getDeviceRecord(id: string) {
  return defHttp.get({
    url: `${recordUrl}/${id}`,
  });
}

/**
 * 新增设备维保记录
 * @param data 维保记录数据
 * @returns 操作结果
 */
export function createDeviceRecord(data: any) {
  return defHttp.post({
    url: recordUrl,
    data,
  });
}

/**
 * 修改设备维保记录
 * @param data 维保记录数据
 * @returns 操作结果
 */
export function updateDeviceRecord(data: any) {
  return defHttp.put({
    url: recordUrl,
    data,
  });
}

/**
 * 删除设备维保记录
 * @param id 维保记录ID
 * @returns 操作结果
 */
export function deleteDeviceRecord(id: string) {
  return defHttp.delete({
    url: `${recordUrl}/${id}`,
  });
}

/**
 * 批量删除设备维保记录
 * @param ids 维保记录ID数组
 * @returns 操作结果
 */
export function batchDeleteDeviceRecords(ids: string[]) {
  return defHttp.delete({
    url: `${recordUrl}/batch`,
    params: { ids },
  });
}
