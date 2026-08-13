import { describe, expect, it } from 'vitest';
import { normalizeHttpError } from './error';

describe('normalizeHttpError', () => {
  it('keeps the established ERP business error envelope', () => {
    expect(normalizeHttpError({ code: 500, msg: '库存不足！' }, 409)).toEqual({
      code: 500,
      message: '库存不足！',
    });
  });

  it('uses validation codes and messages from the ERP response', () => {
    expect(normalizeHttpError({ code: 400, msg: 'ID不能为空！' }, 400)).toEqual({
      code: 400,
      message: 'ID不能为空！',
    });
  });

  it('understands Spring fallback error responses', () => {
    expect(normalizeHttpError({ status: 405, error: 'Method Not Allowed' })).toEqual({
      code: 405,
      message: 'Method Not Allowed',
    });
  });

  it('understands nested client error messages', () => {
    expect(normalizeHttpError({ error: { message: '网关拒绝请求' } }, 502)).toEqual({
      code: 502,
      message: '网关拒绝请求',
    });
  });

  it('falls back safely for a malformed response body', () => {
    expect(normalizeHttpError('<html>bad gateway</html>', 502)).toEqual({
      code: 502,
      message: '网络请求错误，请稍后重试！',
    });
  });
});
