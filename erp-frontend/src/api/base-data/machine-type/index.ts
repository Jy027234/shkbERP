import { defHttp } from '/@/utils/http/axios';
import { PageResult } from '@/api/model/pageResult';
import { ContentTypeEnum } from '@/enums/httpEnum';
import { MachineTypeSelectorVo } from '@/api/base-data/machine-type/model/machineTypeSelectorVo';
import { MachineTypeSelectorBo } from '@/api/base-data/machine-type/model/machineTypeSelectorBo';
import { UpdateMachineTypeVo } from '@/api/base-data/machine-type/model/updateMachineTypeVo';
import { CreateMachineTypeVo } from '@/api/base-data/machine-type/model/createMachineTypeVo';
import { GetMachineTypeBo } from '@/api/base-data/machine-type/model/getMachineTypeBo';
import { QueryMachineTypeVo } from '@/api/base-data/machine-type/model/queryMachineTypeVo';
import { QueryMachineTypeBo } from '@/api/base-data/machine-type/model/queryMachineTypeBo';

const baseUrl = '/basedata/machinetype';
const selectorBaseUrl = '/selector';
const region = 'cloud-api';

export function selector(
  params: MachineTypeSelectorVo,
): Promise<PageResult<MachineTypeSelectorBo>> {
  return defHttp.get<PageResult<MachineTypeSelectorBo>>(
    {
      url: selectorBaseUrl + '/machinetype',
      params,
    },
    {
      region,
    },
  );
}

export function loadMachineType(ids: string[]): Promise<MachineTypeSelectorBo[]> {
  return defHttp.post<MachineTypeSelectorBo[]>(
    {
      url: selectorBaseUrl + '/machinetype/load',
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
export function query(params: QueryMachineTypeVo): Promise<PageResult<QueryMachineTypeBo>> {
  return defHttp.get<PageResult<QueryMachineTypeBo>>(
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
export function get(id: string): Promise<GetMachineTypeBo> {
  return defHttp.get<GetMachineTypeBo>(
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
export function create(data: CreateMachineTypeVo): Promise<void> {
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
export function update(data: UpdateMachineTypeVo): Promise<void> {
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

/**
 * 删除
 */
export function deleteById(id: string): Promise<void> {
  return defHttp.delete<void>(
    {
      url: `${baseUrl}/delete/${id}`,
    },
    {
      region,
    },
  );
}
