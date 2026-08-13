import { beforeEach, describe, expect, test, vi } from 'vitest';
import { ContentTypeEnum } from '@/enums/httpEnum';
import { defHttp } from '/@/utils/http/axios';
import { getCaptchaRequireApi, getTenantRequireApi, loginApi } from '../user';

vi.mock('/@/utils/http/axios', () => ({
  defHttp: {
    delete: vi.fn(),
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
  },
}));

describe('authentication API contracts', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('submits login as form data through the cloud API region', () => {
    const params = {
      tenantName: '测试租户',
      username: 'admin',
      password: 'admin',
      sn: '',
      captcha: '',
    };

    loginApi(params);

    expect(defHttp.post).toHaveBeenCalledWith(
      { url: '/auth/login', params },
      {
        contentType: ContentTypeEnum.FORM_URLENCODED,
        region: 'cloud-api',
      },
    );
  });

  test('keeps captcha parameters in the request and transport options separately', () => {
    getCaptchaRequireApi('测试租户', 'admin');

    expect(defHttp.post).toHaveBeenCalledWith(
      {
        url: '/auth/captcha/require',
        params: { tenantName: '测试租户', username: 'admin' },
      },
      {
        contentType: ContentTypeEnum.FORM_URLENCODED,
        region: 'cloud-api',
      },
    );
  });

  test('routes tenant detection through the cloud API region', () => {
    getTenantRequireApi();

    expect(defHttp.get).toHaveBeenCalledWith(
      { url: '/auth/tenant/require' },
      { region: 'cloud-api' },
    );
  });
});
