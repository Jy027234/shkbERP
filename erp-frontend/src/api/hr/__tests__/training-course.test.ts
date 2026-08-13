import { beforeEach, describe, expect, test, vi } from 'vitest';
import { defHttp } from '@/utils/http/axios';
import {
  exportList,
  getTrainingCourse,
  queryTrainingCourses,
  uploadCourseFile,
} from '../training-course';

vi.mock('@/utils/http/axios', () => ({
  defHttp: {
    delete: vi.fn(),
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
  },
}));

describe('training course API contracts', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('keeps the shkb region outside the Axios request config', () => {
    const query = { page: 1, pageSize: 20 };

    queryTrainingCourses(query);

    expect(defHttp.post).toHaveBeenCalledWith(
      { url: '/training-course/query', data: query },
      { region: 'shkb' },
    );
    expect(vi.mocked(defHttp.post).mock.calls[0][0]).not.toHaveProperty('requestOptions');
  });

  test('uses a path parameter for course detail', () => {
    getTrainingCourse('COURSE-1');

    expect(defHttp.get).toHaveBeenCalledWith(
      { url: '/training-course/COURSE-1' },
      { region: 'shkb' },
    );
  });

  test('preserves native blob responses for exports', () => {
    const query = { keyword: '安全' };

    exportList(query);

    expect(defHttp.post).toHaveBeenCalledWith(
      {
        url: '/training-course/export',
        data: query,
        responseType: 'blob',
      },
      {
        region: 'shkb',
        isReturnNativeResponse: true,
      },
    );
  });

  test('uploads files without overriding the browser FormData transform', () => {
    const file = new File(['course document'], 'course.txt', { type: 'text/plain' });

    uploadCourseFile('COURSE-1', file);

    const [request, options] = vi.mocked(defHttp.post).mock.calls[0];
    expect(request.url).toBe('/training-course/file/upload');
    expect(request.data).toBeInstanceOf(FormData);
    expect(request.data.get('courseId')).toBe('COURSE-1');
    expect(request.data.get('file')).toBe(file);
    expect(request.transformRequest?.[0](request.data)).toBe(request.data);
    expect(options).toEqual({ region: 'shkb' });
  });
});
