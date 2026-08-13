import { defHttp } from '@/utils/http/axios';

const baseUrl = '/shkb/product/storage';

export function query(params: any) {
  return defHttp.get({
    url: `${baseUrl}/query`,
    params,
  });
}

export function create(data: any) {
  return defHttp.post({
    url: baseUrl,
    data,
  });
}

export function get(id: string) {
  return defHttp.get({
    url: `${baseUrl}/${id}`,
  });
}

export function update(data: any) {
  return defHttp.put({
    url: baseUrl,
    data,
  });
}

export function deleteById(id: string) {
  return defHttp.delete({
    url: `${baseUrl}/${id}`,
  });
}

export function batchDelete(ids: string[]) {
  return defHttp.delete({
    url: `${baseUrl}/batch`,
    params: { ids },
  });
}
