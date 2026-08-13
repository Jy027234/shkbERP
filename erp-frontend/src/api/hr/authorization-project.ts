import { defHttp } from '@/utils/http/axios';

const region = 'shkb';

// 查询授权项目列表
export function query(params: any) {
  return defHttp.post(
    {
      url: '/shkb/authorization-project/query',
      data: params,
    },
    { region },
  );
}

// 获取授权项目详情
export function get(id: string) {
  return defHttp.get(
    {
      url: `/shkb/authorization-project/${id}`,
    },
    { region },
  );
}

// 根据ID列表加载授权项目（用于选择器回显）
export function loadAuthorizationProjects(ids: string[]) {
  return defHttp.post(
    {
      url: '/shkb/authorization-project/load',
      data: ids,
    },
    { region },
  );
}

// 创建授权项目
export function create(data: any) {
  return defHttp.post({
    url: '/shkb/authorization-project',
    data,
  });
}

// 修改授权项目
export function update(data: any) {
  return defHttp.put({
    url: '/shkb/authorization-project',
    data,
  });
}

// 删除授权项目
export function del(id: string) {
  return defHttp.delete({
    url: `/shkb/authorization-project/${id}`,
  });
}

// 批量删除授权项目
export function batchDelete(ids: string[]) {
  return defHttp.delete({
    url: '/shkb/authorization-project/batch',
    data: ids,
  });
}

// 查询所有启用的授权项目
export function getEnabledList() {
  return defHttp.get({
    url: '/shkb/authorization-project/list/enabled',
  });
}

// 获取授权项目的必修课程
export function getRequiredCourses(projectId: string) {
  return defHttp.get<string[]>({
    url: `/shkb/authorization-project/required-courses/${projectId}`,
  });
}

// 保存授权项目的必修课程
export function saveRequiredCourses(projectId: string, courseIds: string[]) {
  return defHttp.post({
    url: `/shkb/authorization-project/required-courses/${projectId}`,
    data: courseIds,
  });
}

// 导出授权项目
export function exportList(data: any) {
  return defHttp.post<Blob>(
    {
      url: '/shkb/authorization-project/export',
      data,
      responseType: 'blob',
    },
    {
      region,
      isReturnNativeResponse: true,
    },
  );
}
