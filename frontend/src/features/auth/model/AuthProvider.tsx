import { useQueryClient } from '@tanstack/react-query'
import { useCallback, useEffect, useMemo, useState } from 'react'
import type { ReactNode } from 'react'
import { findUserByUserNm } from '@/features/auth/api/authApi'
import { AuthContext } from '@/features/auth/model/AuthContext'
import type { AuthStatus } from '@/features/auth/model/AuthContext'
import { authStorage } from '@/features/auth/model/authStorage'

interface AuthProviderProps {
  children: ReactNode
}

interface AuthState {
  status: AuthStatus
  userId: string | null
  userNm: string | null
}

export function AuthProvider({ children }: AuthProviderProps) {
  const queryClient = useQueryClient()
  const [authState, setAuthState] = useState<AuthState>(() => {
    const storedUserNm = authStorage.getUserNm()

    return storedUserNm
      ? { status: 'checking', userId: null, userNm: null }
      : { status: 'anonymous', userId: null, userNm: null }
  })

  useEffect(() => {
    const storedUserNm = authStorage.getUserNm()

    if (!storedUserNm) {
      return
    }

    const userNmToRestore = storedUserNm
    const abortController = new AbortController()

    async function restoreAuthentication() {
      try {
        const user = await findUserByUserNm(userNmToRestore, abortController.signal)
        authStorage.setUserNm(user.userNm)
        setAuthState({ status: 'authenticated', userId: user.userId, userNm: user.userNm })
      } catch (error) {
        if (error instanceof DOMException && error.name === 'AbortError') {
          return
        }

        authStorage.removeUserNm()
        queryClient.clear()
        setAuthState({ status: 'anonymous', userId: null, userNm: null })
      }
    }

    void restoreAuthentication()

    return () => abortController.abort()
  }, [queryClient])

  const login = useCallback(
    async (userNm: string) => {
      const user = await findUserByUserNm(userNm)

      queryClient.clear()
      authStorage.setUserNm(user.userNm)
      setAuthState({ status: 'authenticated', userId: user.userId, userNm: user.userNm })
    },
    [queryClient],
  )

  const logout = useCallback(() => {
    authStorage.removeUserNm()
    queryClient.clear()
    setAuthState({ status: 'anonymous', userId: null, userNm: null })
  }, [queryClient])

  const contextValue = useMemo(
    () => ({
      status: authState.status,
      userId: authState.userId,
      userNm: authState.userNm,
      login,
      logout,
    }),
    [authState, login, logout],
  )

  return <AuthContext value={contextValue}>{children}</AuthContext>
}
