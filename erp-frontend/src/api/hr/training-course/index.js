import request from '@/utils/request';

// 查询培训课程列表
export const queryTrainingCourses = (params) => {
  return request({
    url: '/api/training-course/query',
    method: 'post',
    data: params
  });
};

// 查询所有启用的课程
export const queryEnabledCourses = () => {
  return request({
    url: '/api/training-course/list/enabled',
    method: 'get'
  });
};

// 获取所有有效课程（用于下拉选择）
export const getAllValidCourses = () => {
  return request({
    url: '/api/training-course/all-valid',
    method: 'get'
  });
};

// 查询课程详情
export const getTrainingCourse = (id) => {
  return request({
    url: `/api/training-course/${id}`,
    method: 'get'
  });
};

// 创建课程
export const createTrainingCourse = (params) => {
  return request({
    url: '/api/training-course',
    method: 'post',
    data: params
  });
};

// 修改课程
export const updateTrainingCourse = (params) => {
  return request({
    url: '/api/training-course',
    method: 'put',
    data: params
  });
};

// 启用/禁用课程
export const changeCourseStatus = (id, status) => {
  return request({
    url: '/api/training-course/status',
    method: 'put',
    params: {
      id,
      status
    }
  });
};

// 删除课程
export const deleteTrainingCourse = (id) => {
  return request({
    url: `/api/training-course/${id}`,
    method: 'delete'
  });
};

// 批量删除课程
export const batchDeleteTrainingCourses = (ids) => {
  return request({
    url: '/api/training-course/batch',
    method: 'delete',
    data: ids
  });
};

// 查询课程文档列表
export const getCourseFileList = (courseId) => {
  return request({
    url: '/api/training-course/file/list',
    method: 'get',
    params: {
      courseId
    }
  });
};

// 上传课程文档
export const uploadCourseFile = (formData) => {
  return request({
    url: '/api/training-course/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

// 删除课程文档
export const deleteCourseFile = (id) => {
  return request({
    url: `/api/training-course/file/${id}`,
    method: 'delete'
  });
};

// 批量删除课程文档
export const batchDeleteCourseFiles = (ids) => {
  return request({
    url: '/api/training-course/file/batch',
    method: 'delete',
    data: ids
  });
};
