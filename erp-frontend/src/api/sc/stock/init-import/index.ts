import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum, ResponseEnum } from '@/enums/httpEnum';

const baseUrl = '/stock/init/import';
const region = 'cloud-api';

// 下载库存初始化导入模板
export function downloadTemplate(): Promise<void> {
  return defHttp.get<void>(
    {
      url: baseUrl + '/template',
    },
    {
      responseType: ResponseEnum.BLOB,
      region,
    },
  );
}

// 预检上传（不落库）
export function precheck(data: { id: string; file: Blob; initOnly?: boolean }): Promise<any> {
  return defHttp.post<any>(
    {
      url: baseUrl + '/precheck',
      data,
    },
    {
      contentType: ContentTypeEnum.BLOB,
      region,
    },
  );
}

// 执行导入（按批次，仅处理未成功项）
export function execute(data: { batchId: string; id: string; initOnly?: boolean }): Promise<any> {
  return defHttp.post<any>(
    {
      url: baseUrl + '/execute',
      data,
    },
    {
      contentType: ContentTypeEnum.FORM_URLENCODED,
      region,
    },
  );
}
