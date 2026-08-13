import { defHttp } from '@/utils/http/axios';

// 后端网关前缀在非 axios 场景（如 <img src>）需要显式加 /api
const baseUrl = '/machine/task/magnetic';
const apiPrefix = '/api';

/**
 * 查询磁粉机任务列表
 */
export function query(params: any) {
  return defHttp.get({
    url: `${baseUrl}/query`,
    params,
  });
}

/**
 * 下发任务
 */
export function send(taskId: string) {
  return defHttp.post({
    url: `${baseUrl}/send?taskId=${encodeURIComponent(taskId)}`,
    timeout: 5000,
  });
}

/**
 * 获取远程设备文件夹列表
 */
export function folders() {
  return defHttp.get({
    url: `${baseUrl}/folders`,
  });
}

/**
 * 获取远程设备指定文件夹的文件列表
 */
export function files(folder: string) {
  return defHttp.get({
    url: `${baseUrl}/files`,
    params: { folder },
  });
}

/**
 * 获取远程图片的直链（用于<img>展示）
 */
export function imageUrl(path: string, overlay?: boolean, thumb?: boolean) {
  const usp = new URLSearchParams();
  // 直接交给 URLSearchParams 进行编码，避免二次编码
  usp.set('path', path);
  if (overlay !== undefined) usp.set('overlay', String(overlay));
  if (thumb !== undefined) usp.set('thumb', String(thumb));
  // 返回带 /api 前缀的绝对路径，适配前端代理到后端
  return `${apiPrefix}${baseUrl}/image?${usp.toString()}`;
}
