import { defHttp } from '/@/utils/http/axios';

const baseUrl = '/shkb/device';
const region = 'cloud-api';

// 获取设备附件列表
export function getDeviceAttachments(deviceId: string) {
  return defHttp.get(
    {
      url: `${baseUrl}/attachment/list`,
      params: { deviceId },
    },
    { region },
  );
}

// 上传设备附件
export function uploadDeviceAttachments(deviceId: string, files: File[]) {
  const formData = new FormData();
  formData.append('deviceId', deviceId);
  if (files && files.length) {
    files.forEach((f) => formData.append('files', f));
  }
  return defHttp.post(
    {
      url: `${baseUrl}/attachment/upload`,
      data: formData,
      headers: { 'X-Requested-With': 'XMLHttpRequest' },
      transformRequest: [(data) => data],
    },
    { region, isTransformResponse: true, joinParamsToUrl: false },
  );
}

// 删除设备附件
export function deleteDeviceAttachment(id: string) {
  return defHttp.delete(
    {
      url: `${baseUrl}/attachment/${id}`,
    },
    { region },
  );
}
