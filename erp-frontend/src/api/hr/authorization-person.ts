import { defHttp } from '@/utils/http/axios';

const baseUrl = '/shkb/person-authorization';

// 查询人员授权列表
export function query(params: any) {
  return defHttp.post({
    url: `${baseUrl}/query`,
    data: params,
  });
}

// 获取人员授权详情
export function get(id: string) {
  return defHttp.get({
    url: `${baseUrl}/${id}`,
  });
}

// 创建人员授权
export function create(data: any) {
  // 如果是FormData类型，使用特殊处理方式
  if (data instanceof FormData) {
    return defHttp.post(
      {
        url: baseUrl,
        data,
        headers: {
          // 不要手动设置Content-Type，让浏览器自动设置带boundary的Content-Type
          'X-Requested-With': 'XMLHttpRequest',
        },
        // 确保不会将FormData转换为其他格式
        transformRequest: [(data) => data],
      },
      {
        // 确保响应被正确处理
        isTransformResponse: true,
        // 确保参数不会被拼接到URL
        joinParamsToUrl: false,
      },
    );
  } else {
    // 普通JSON数据处理
    return defHttp.post({
      url: baseUrl,
      data,
    });
  }
}

// 更新人员授权基本信息
export function update(id: string, description: string) {
  return defHttp.put({
    url: `${baseUrl}?id=${id}&description=${encodeURIComponent(description)}`,
  });
}

// 更新人员授权项目
export function updateProjects(id: string, projects: any[]) {
  return defHttp.put({
    url: `${baseUrl}/projects?id=${id}`,
    data: projects,
  });
}

// 删除人员授权
export function del(id: string) {
  return defHttp.delete({
    url: `${baseUrl}/${id}`,
  });
}

// 批量删除人员授权
export function batchDelete(ids: string[]) {
  return defHttp.delete({
    url: `${baseUrl}/batch`,
    params: {
      ids: ids.join(','),
    },
  });
}

// 延期授权项目
export function extend(id: string, projectId: string, expiryDate: string) {
  return defHttp.post({
    url: `${baseUrl}/extend/${id}`,
    params: { projectId, expiryDate },
  });
}

// 撤销
export function revoke(id: string) {
  return defHttp.post({
    url: `${baseUrl}/revoke/${id}`,
  });
}

// 上传人员授权附件
export function uploadFile(authorizationId: string, files: File[]) {
  const formData = new FormData();
  formData.append('authorizationId', authorizationId);
  files.forEach(file => {
    formData.append('files', file);
  });

  return defHttp.post({
    url: `${baseUrl}/file/upload`,
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}

// 获取人员授权附件列表
export function getFileList(authorizationId: string) {
  return defHttp.get({
    url: `${baseUrl}/file/list`,
    params: {
      authorizationId,
    },
  });
}

// 删除人员授权附件
export function deleteFile(id: string) {
  return defHttp.delete({
    url: `${baseUrl}/file/${id}`,
  });
}

// 下载人员授权附件
export function downloadFile(id: string) {
  return defHttp.get(
    {
      url: `${baseUrl}/file/download/${id}`,
    },
    {
      isReturnNativeResponse: true,
    },
  );
}

// 检查授权有效性
export function checkValidity(id: string) {
  return defHttp.get({
    url: `${baseUrl}/check-validity/${id}`,
  });
}
