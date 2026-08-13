import { beforeEach, describe, expect, test, vi } from 'vitest';
import { defHttp } from '/@/utils/http/axios';
import { batchAddWorkCards, getWorkCards } from '../../contract-task/work-card';
import { batchAddProducts, create, getProducts, query, update } from '../index';

vi.mock('/@/utils/http/axios', () => ({
  defHttp: {
    delete: vi.fn(),
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
  },
}));

describe('work-card API contracts', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('sends the backend-supported work-card filters as query parameters', () => {
    const params = {
      code: 'WC-1',
      machineTypeId: 'MT-1',
      partNumberCode: 'PN-1',
      repairTypeId: 'RT-1',
      available: true,
      pageIndex: 1,
      pageSize: 20,
    };

    query(params);

    expect(defHttp.get).toHaveBeenCalledWith(
      { url: '/shkb/work-card/query', params },
      { region: 'cloud-api' },
    );
  });

  test('creates and updates work cards as JSON bodies', () => {
    const card = {
      code: 'WC-1',
      name: 'Inspection',
      partNumberId: 'PN-1',
      repairTypeId: 'RT-1',
      available: true,
      version: 'A',
    };

    create(card);
    update({ id: 'CARD-1', ...card });

    expect(defHttp.post).toHaveBeenNthCalledWith(
      1,
      { url: '/shkb/work-card', data: card },
      { region: 'cloud-api' },
    );
    expect(defHttp.post).toHaveBeenNthCalledWith(
      2,
      { url: '/shkb/work-card/update', data: { id: 'CARD-1', ...card } },
      { region: 'cloud-api' },
    );
  });

  test('keeps work-card product reads and writes on their documented routes', () => {
    getProducts('CARD-1');
    batchAddProducts({ workCardId: 'CARD-1', productIds: ['PRODUCT-1'] });

    expect(defHttp.get).toHaveBeenCalledWith(
      { url: '/shkb/work-card/products', params: { workCardId: 'CARD-1' } },
      { region: 'cloud-api' },
    );
    expect(defHttp.post).toHaveBeenCalledWith(
      {
        url: '/shkb/work-card/product/add',
        data: { workCardId: 'CARD-1', productIds: ['PRODUCT-1'] },
      },
      { region: 'cloud-api' },
    );
  });

  test('uses the task work-card association endpoints without changing payload shape', () => {
    const payload = { taskId: 'TASK-1', workCardIds: ['CARD-1'] };

    getWorkCards('TASK-1');
    batchAddWorkCards(payload);

    expect(defHttp.get).toHaveBeenCalledWith(
      { url: '/shkb/contract-task/work-cards', params: { taskId: 'TASK-1' } },
      { region: 'cloud-api' },
    );
    expect(defHttp.post).toHaveBeenCalledWith(
      { url: '/shkb/contract-task/work-card/add', data: payload },
      { region: 'cloud-api' },
    );
  });
});
