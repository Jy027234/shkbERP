import { defHttp } from '/@/utils/http/axios';

const baseUrl = '/shkb/contract-task';
const region = 'cloud-api';

// 获取放行文件列表
export function getApprovalFiles(taskId: string) {
  return defHttp.get(
    {
      url: `${baseUrl}/approval-file/list`,
      params: { taskId },
    },
    { region },
  );
}

// 上传放行文件
export function uploadApprovalFiles(taskId: string, files: File[]) {
  const formData = new FormData();
  formData.append('taskId', taskId);
  if (files && files.length) {
    files.forEach((f) => formData.append('files', f));
  }
  return defHttp.post(
    {
      url: `${baseUrl}/approval-file/upload`,
      data: formData,
      headers: { 'X-Requested-With': 'XMLHttpRequest' },
      transformRequest: [(data) => data],
    },
    { region, isTransformResponse: true, joinParamsToUrl: false },
  );
}

// 删除放行文件
export function deleteApprovalFile(id: string) {
  return defHttp.delete(
    {
      url: `${baseUrl}/approval-file/${id}`,
    },
    { region },
  );
}
