import { apiClient } from '@/shared/api/apiClient'

export interface AuthenticatedUser {
  userId: string
}

export function findUser(userId: string, signal?: AbortSignal) {
  return apiClient.get<AuthenticatedUser>(`/users/${encodeURIComponent(userId)}`, signal)
}
