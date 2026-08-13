import { defHttp } from '/@/utils/http/axios';
import { PageResult } from '@/api/model/pageResult';
import { ContentTypeEnum } from '@/enums/httpEnum';
import { PartNumberSelectorBo } from '@/api/base-data/part-number/model/partNumberSelectorBo';
import { PartNumberSelectorVo } from '@/api/base-data/part-number/model/partNumberSelectorVo';
import { UpdatePartNumberVo } from '@/api/base-data/part-number/model/updatePartNumberVo';
import { CreatePartNumberVo } from '@/api/base-data/part-number/model/createPartNumberVo';
import { GetPartNumberBo } from '@/api/base-data/part-number/model/getPartNumberBo';
import { QueryPartNumberVo } from '@/api/base-data/part-number/model/queryPartNumberVo';
import { QueryPartNumberBo } from '@/api/base-data/part-number/model/queryPartNumberBo';

const baseUrl = '/basedata/partnumber';
const selectorBaseUrl = '/selector';
const region = 'cloud-api';

export function selector(params: PartNumberSelectorVo): Promise<PageResult<PartNumberSelectorBo>> {
  return defHttp.get<PageResult<PartNumberSelectorBo>>(
    {
      url: selectorBaseUrl + '/partnumber',
      params,
    },
    {
      region,
    },
  );
}

export function loadPartNumber(ids: string[]): Promise<PartNumberSelectorBo[]> {
  return defHttp.post<PartNumberSelectorBo[]>(
    {
      url: selectorBaseUrl + '/partnumber/load',
      data: ids,
    },
    {
      contentType: ContentTypeEnum.JSON,
      region,
    },
  );
}

/**
 * 查询列表
 */
export function query(params: QueryPartNumberVo): Promise<PageResult<QueryPartNumberBo>> {
  return defHttp.get<PageResult<QueryPartNumberBo>>(
    {
      url: baseUrl + '/query',
      params,
    },
    {
      region,
    },
  );
}

/**
 * 根据ID查询
 * @param id
 */
export function get(id: string): Promise<GetPartNumberBo> {
  return defHttp.get<GetPartNumberBo>(
    {
      url: baseUrl,
      params: {
        id: id,
      },
    },
    {
      region,
    },
  );
}

/**
 * 新增
 * @param data
 */
export function create(data: CreatePartNumberVo): Promise<void> {
  return defHttp.post<void>(
    {
      url: baseUrl,
      data,
    },
    {
      contentType: ContentTypeEnum.FORM_URLENCODED,
      region,
    },
  );
}

/**
 * 修改
 * @param data
 */
export function update(data: UpdatePartNumberVo): Promise<void> {
  return defHttp.put<void>(
    {
      url: baseUrl,
      data,
    },
    {
      contentType: ContentTypeEnum.FORM_URLENCODED,
      region,
    },
  );
}
