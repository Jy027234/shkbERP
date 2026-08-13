import { defHttp } from '@/utils/http/axios';

const region = 'shkb';

export interface DeptSimple {
  id: string;
  name: string;
}

// 查询员工列表
export function query(params: any) {
  return defHttp.get({
    url: '/shkb/employee/query',
    params,
  });
}

// 获取员工详情
export function get(id: string) {
  return defHttp.get({
    url: `/shkb/employee/${id}`,
  });
}

// 创建员工
export function create(data: any) {
  return defHttp.post({
    url: '/shkb/employee',
    data,
  });
}

// 更新员工
export function update(data: any) {
  return defHttp.put({
    url: '/shkb/employee',
    data,
  });
}

// 删除员工
export function del(id: string) {
  return defHttp.delete({
    url: `/shkb/employee/${id}`,
  });
}

// 批量删除员工
export function batchDelete(ids: string[]) {
  return defHttp.delete({
    url: '/shkb/employee/batch',
    params: {
      ids: ids.join(','),
    },
  });
}

// 员工离职
export function leave(data: any) {
  return defHttp.put({
    url: '/shkb/employee/leave',
    data,
  });
}

// 更新员工离职状态
export function leaveStatus(data: any) {
  return defHttp.put({
    url: '/shkb/employee/leave-status',
    data,
  });
}

// 批量更新员工离职状态
export function batchLeaveStatus(data: any) {
  return defHttp.put({
    url: '/shkb/employee/batch-leave-status',
    data,
  });
}

// 离职登记（只更新日期和原因，不修改状态）
export function updateLeaveInfo(data: any) {
  return defHttp.put({
    url: '/shkb/employee/leave-info',
    data,
  });
}

// 获取员工统计数据
export function getStatistics() {
  return defHttp.get({
    url: '/shkb/employee/statistics',
  });
}

// 导出员工列表
export function exportList(data: any) {
  return defHttp.post<Blob>(
    {
      url: '/shkb/employee/export',
      data,
      responseType: 'blob',
    },
    {
      region,
      isReturnNativeResponse: true,
    },
  );
}

// 获取部门列表
export function getDepts() {
  return defHttp.get<DeptSimple[]>({
    url: '/shkb/employee/depts',
  });
}

// 上传员工照片
export function uploadPhoto(id: string, file: File) {
  const formData = new FormData();
  formData.append('file', file);
  return defHttp.post<string>({
    url: `/shkb/employee/${id}/photo`,
    data: formData,
    transformRequest: [(data) => data],
  });
}

// 获取员工资质证书列表
export function getCertificates(employeeId: string) {
  return defHttp.get({
    url: `/shkb/employee-certificate/employee/${employeeId}`,
  });
}

// 新增员工资质证书
export function addCertificate(data: any) {
  return defHttp.post({
    url: `/shkb/employee-certificate`,
    data,
  });
}

// 更新员工资质证书
export function updateCertificate(data: any) {
  return defHttp.put({
    url: `/shkb/employee-certificate`,
    data,
  });
}

// 删除员工资质证书
export function deleteCertificate(certificateId: string) {
  return defHttp.delete({
    url: `/shkb/employee-certificate/${certificateId}`,
  });
}

// 获取员工培训记录列表
export function getTrainings(employeeId: string) {
  return defHttp.get({
    url: `/shkb/employee-training/employee/${employeeId}`,
  });
}

// 获取员工附件资料列表
export function getFiles(employeeId: string) {
  return defHttp.get({
    url: `/shkb/employee-file/list`,
    params: { employeeId },
  });
}

// 上传员工附件资料
export function uploadFile(employeeId: string, file: File) {
  const formData = new FormData();
  formData.append('employeeId', employeeId);
  formData.append('files', file);
  return defHttp.post({
    url: `/shkb/employee-file/upload`,
    data: formData,
    transformRequest: [(data) => data],
  });
}

// 删除员工附件资料
export function deleteFile(fileId: string) {
  return defHttp.delete({
    url: `/shkb/employee-file/${fileId}`,
  });
}

// 下载员工附件资料
export function downloadFile(fileId: string) {
  return defHttp.get({
    url: `/shkb/employee-file/download/${fileId}`,
    responseType: 'blob',
  });
}