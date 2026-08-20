import { ApiError } from '@/shared/api/ApiError'
import type { ApiErrorResponse, ApiResponse } from '@/shared/api/apiTypes'
import { env } from '@/shared/config/env'

interface ApiRequestOptions extends Omit<RequestInit, 'body'> {
  body?: unknown
}

async function request<T>(path: string, options: ApiRequestOptions = {}): Promise<T> {
  const { body, headers: customHeaders, ...requestOptions } = options
  const headers = new Headers(customHeaders)

  headers.set('Accept', 'application/json')

  if (body !== undefined) {
    headers.set('Content-Type', 'application/json')
  }

  let response: Response

  try {
    response = await fetch(`${env.apiBaseUrl}${path}`, {
      ...requestOptions,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
    })
  } catch (error) {
    if (error instanceof DOMException && error.name === 'AbortError') {
      throw error
    }

    throw new ApiError(0, 'NETWORK_ERROR', '서버에 연결할 수 없습니다.')
  }

  const responseBody = await parseResponseBody(response)

  if (!response.ok) {
    const errorResponse = responseBody as ApiErrorResponse | null

    throw new ApiError(
      response.status,
      errorResponse?.code ?? 'UNKNOWN_ERROR',
      errorResponse?.message ?? '요청 처리 중 오류가 발생했습니다.',
      errorResponse?.fieldErrors ?? [],
    )
  }

  if (responseBody === null) {
    return undefined as T
  }

  const apiResponse = responseBody as ApiResponse<T>

  return apiResponse.data
}

async function parseResponseBody(response: Response): Promise<unknown | null> {
  if (response.status === 204) {
    return null
  }

  const text = await response.text()

  if (!text) {
    return null
  }

  try {
    return JSON.parse(text) as unknown
  } catch {
    return null
  }
}

export const apiClient = {
  get<T>(path: string, signal?: AbortSignal) {
    return request<T>(path, {
      method: 'GET',
      signal,
    })
  },

  post<T>(path: string, body?: unknown, signal?: AbortSignal) {
    return request<T>(path, {
      method: 'POST',
      body,
      signal,
    })
  },

  patch<T>(path: string, body?: unknown, signal?: AbortSignal) {
    return request<T>(path, {
      method: 'PATCH',
      body,
      signal,
    })
  },

  delete<T>(path: string, signal?: AbortSignal) {
    return request<T>(path, {
      method: 'DELETE',
      signal,
    })
  },
}
