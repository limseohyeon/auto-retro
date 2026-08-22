import { useContext } from 'react'
import { AuthContext } from '@/features/auth/model/AuthContext'

export function useAuth() {
  const authContext = useContext(AuthContext)

  if (!authContext) {
    throw new Error('useAuth는 AuthProvider 안에서 사용해야 합니다.')
  }

  return authContext
}
