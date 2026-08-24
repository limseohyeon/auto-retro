import { apiClient } from '@/shared/api/apiClient'

export interface AuthenticatedUser {
  userId: string
  userNm: string
}

export function findUserByUserNm(userNm: string, signal?: AbortSignal) {
  return apiClient.get<AuthenticatedUser>(`/users/${encodeURIComponent(userNm)}`, signal)
}
