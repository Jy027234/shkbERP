import { defHttp } from '@/utils/http/axios';

const region = 'shkb';

// 查询培训课程列表
export function queryTrainingCourses(params: any) {
  return defHttp.post(
    {
      url: '/training-course/query',
      data: params,
    },
    { region },
  );
}

// 查询所有启用的课程
export function queryEnabledCourses() {
  return defHttp.get(
    {
      url: '/training-course/list/enabled',
    },
    { region },
  );
}

// 获取所有有效课程（用于下拉选择）
export function getAllValidCourses() {
  return defHttp.get(
    {
      url: '/training-course/all-valid',
    },
    { region },
  );
}

// 查询课程详情
export function getTrainingCourse(id: string) {
  return defHttp.get(
    {
      url: `/training-course/${id}`,
    },
    { region },
  );
}

// 根据ID列表加载课程详情（用于选择器回显）
export function loadTrainingCourses(ids: string[]) {
  return defHttp.post(
    {
      url: '/training-course/load',
      data: ids,
    },
    { region },
  );
}

// 创建课程
export function createTrainingCourse(data: any) {
  return defHttp.post(
    {
      url: '/training-course',
      data,
    },
    { region },
  );
}

// 修改课程
export function updateTrainingCourse(data: any) {
  return defHttp.put(
    {
      url: '/training-course',
      data,
    },
    { region },
  );
}

// 启用/禁用课程
export function changeCourseStatus(id: string, status: number) {
  return defHttp.put(
    {
      url: '/training-course/status',
      params: {
        id,
        status,
      },
    },
    { region },
  );
}

// 删除课程
export function deleteTrainingCourse(id: string) {
  return defHttp.delete(
    {
      url: `/training-course/${id}`,
    },
    { region },
  );
}

// 批量删除课程
export function batchDeleteTrainingCourses(ids: string[]) {
  return defHttp.delete(
    {
      url: '/training-course/batch',
      data: ids,
    },
    { region },
  );
}

// 查询课程文档列表
export function getCourseFileList(courseId: string) {
  return defHttp.get(
    {
      url: '/training-course/file/list',
      params: {
        courseId,
      },
    },
    { region },
  );
}

// 上传课程文档
export function uploadCourseFile(courseId: string, file: File) {
  const formData = new FormData();
  formData.append('courseId', courseId);
  formData.append('file', file);
  return defHttp.post(
    {
      url: '/training-course/file/upload',
      data: formData,
      transformRequest: [(data) => data],
    },
    { region },
  );
}

// 删除课程文档
export function deleteCourseFile(id: string) {
  return defHttp.delete(
    {
      url: `/training-course/file/${id}`,
    },
    { region },
  );
}

// 批量删除课程文档
export function batchDeleteCourseFiles(ids: string[]) {
  return defHttp.delete(
    {
      url: '/training-course/file/batch',
      data: ids,
    },
    { region },
  );
}

// 导出培训课程列表
export function exportList(data: any) {
  return defHttp.post(
    {
      url: '/training-course/export',
      data,
      responseType: 'blob',
    },
    {
      region: 'shkb',
      isReturnNativeResponse: true,
    },
  );
}
