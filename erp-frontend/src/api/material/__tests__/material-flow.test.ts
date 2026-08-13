import { beforeEach, describe, expect, test, vi } from 'vitest';
import { defHttp } from '/@/utils/http/axios';
import * as materialApplyApi from '../apply';
import * as materialOrderApi from '../order';
import * as materialOutApi from '../out';

vi.mock('/@/utils/http/axios', () => ({
  defHttp: {
    delete: vi.fn(),
    get: vi.fn(),
    patch: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
  },
}));

describe('material flow API contracts', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('queries material applications and orders through the cloud API region', () => {
    materialApplyApi.query({ pageIndex: 1, pageSize: 20, hasMaterialOrder: false });
    materialOrderApi.query({
      pageIndex: 1,
      pageSize: 20,
      sortField: 'createTime',
      sortOrder: 'desc',
      contractCode: 'CONTRACT-1',
      createTimeStart: '2026-08-01 00:00:00',
      isOutFinish: false,
    });

    expect(defHttp.get).toHaveBeenNthCalledWith(
      1,
      {
        url: '/shkb/contract-task/material-apply/query',
        params: { pageIndex: 1, pageSize: 20, hasMaterialOrder: false },
      },
      { region: 'cloud-api' },
    );
    expect(defHttp.get).toHaveBeenNthCalledWith(
      2,
      {
        url: '/material/order/query',
        params: {
          pageIndex: 1,
          pageSize: 20,
          sortField: 'createTime',
          sortOrder: 'desc',
          contractCode: 'CONTRACT-1',
          createTimeStart: '2026-08-01 00:00:00',
          isOutFinish: false,
        },
      },
      { region: 'cloud-api' },
    );
  });

  test('creates an order from an approved application as JSON', () => {
    const data = { materialApplyId: 'APPLY-1', scId: 'SC-1', description: 'smoke' };

    materialOrderApi.createFromApply(data);

    expect(defHttp.post).toHaveBeenCalledWith(
      { url: '/material/order', data },
      { region: 'cloud-api', contentType: 'application/json;charset=UTF-8' },
    );
  });

  test('creates and approves outbound sheets with the backend HTTP methods', () => {
    const sheet = {
      scId: 'SC-1',
      materialOrderId: 'ORDER-1',
      details: [
        {
          productId: 'PRODUCT-1',
          outNum: 1,
          orderNum: 1,
          materialOrderDetailId: 'DETAIL-1',
        },
      ],
    };

    materialOutApi.create(sheet);
    materialOutApi.approvePass({ id: 'SHEET-1', description: 'issued' });

    expect(defHttp.post).toHaveBeenCalledWith(
      { url: '/material/out/sheet', data: sheet },
      { region: 'cloud-api', contentType: 'application/json;charset=UTF-8' },
    );
    expect(defHttp.patch).toHaveBeenCalledWith(
      {
        url: '/material/out/sheet/approve/pass',
        data: { id: 'SHEET-1', description: 'issued' },
      },
      { region: 'cloud-api', contentType: 'application/json;charset=UTF-8' },
    );
  });

  test('exports outbound sheets as a JSON-filtered blob response', () => {
    const filters = { pageIndex: 1, pageSize: 20, status: 0 };

    materialOutApi.exportList(filters);

    expect(defHttp.post).toHaveBeenCalledWith(
      { url: '/material/out/sheet/export', data: filters },
      {
        region: 'cloud-api',
        responseType: 'BLOB',
        contentType: 'application/json;charset=UTF-8',
      },
    );
  });
});
