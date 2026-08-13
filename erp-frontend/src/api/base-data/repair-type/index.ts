import { defHttp } from '/@/utils/http/axios';
import { PageResult } from '@/api/model/pageResult';
import { ContentTypeEnum } from '@/enums/httpEnum';
import { RepairTypeSelectorVo } from '@/api/base-data/repair-type/model/repairTypeSelectorVo';
import { RepairTypeSelectorBo } from '@/api/base-data/repair-type/model/repairTypeSelectorBo';
import { UpdateRepairTypeVo } from '@/api/base-data/repair-type/model/updateRepairTypeVo';
import { CreateRepairTypeVo } from '@/api/base-data/repair-type/model/createRepairTypeVo';
import { GetRepairTypeBo } from '@/api/base-data/repair-type/model/getRepairTypeBo';
import { QueryRepairTypeVo } from '@/api/base-data/repair-type/model/queryRepairTypeVo';
import { QueryRepairTypeBo } from '@/api/base-data/repair-type/model/queryRepairTypeBo';

const baseUrl = '/basedata/repairtype';
const selectorBaseUrl = '/selector';
const region = 'cloud-api';

export function selector(params: RepairTypeSelectorVo): Promise<PageResult<RepairTypeSelectorBo>> {
  return defHttp.get<PageResult<RepairTypeSelectorBo>>(
    {
      url: selectorBaseUrl + '/repairtype',
      params,
    },
    {
      region,
    },
  );
}

export function loadRepairType(ids: string[]): Promise<RepairTypeSelectorBo[]> {
  return defHttp.post<RepairTypeSelectorBo[]>(
    {
      url: selectorBaseUrl + '/repairtype/load',
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
export function query(params: QueryRepairTypeVo): Promise<PageResult<QueryRepairTypeBo>> {
  return defHttp.get<PageResult<QueryRepairTypeBo>>(
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
export function get(id: string): Promise<GetRepairTypeBo> {
  return defHttp.get<GetRepairTypeBo>(
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
export function create(data: CreateRepairTypeVo): Promise<void> {
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
export function update(data: UpdateRepairTypeVo): Promise<void> {
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
