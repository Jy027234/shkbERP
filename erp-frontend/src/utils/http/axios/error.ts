const DEFAULT_HTTP_ERROR_MESSAGE = '网络请求错误，请稍后重试！';

interface ErrorPayload {
  code?: unknown;
  status?: unknown;
  msg?: unknown;
  error?: unknown;
}

export interface NormalizedHttpError {
  code: number;
  message: string;
}

function isErrorPayload(value: unknown): value is ErrorPayload {
  return typeof value === 'object' && value !== null;
}

/**
 * Normalize both the ERP error envelope and Spring's fallback error body.
 */
export function normalizeHttpError(
  value: unknown,
  httpStatus?: number,
): NormalizedHttpError {
  if (!isErrorPayload(value)) {
    return {
      code: httpStatus ?? 0,
      message: DEFAULT_HTTP_ERROR_MESSAGE,
    };
  }

  const code =
    typeof value.code === 'number'
      ? value.code
      : typeof value.status === 'number'
        ? value.status
        : (httpStatus ?? 0);
  let message = typeof value.msg === 'string' ? value.msg : '';

  if (!message && typeof value.error === 'string') {
    message = value.error;
  } else if (!message && isErrorPayload(value.error) && typeof value.error.msg === 'string') {
    message = value.error.msg;
  }

  if (!message && isErrorPayload(value.error)) {
    const nestedMessage = (value.error as { message?: unknown }).message;
    if (typeof nestedMessage === 'string') {
      message = nestedMessage;
    }
  }

  return {
    code,
    message: message || DEFAULT_HTTP_ERROR_MESSAGE,
  };
}
