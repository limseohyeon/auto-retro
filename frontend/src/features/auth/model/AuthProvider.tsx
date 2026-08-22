import { useQueryClient } from '@tanstack/react-query'
import { useCallback, useEffect, useMemo, useState } from 'react'
import type { ReactNode } from 'react'
import { findUser } from '@/features/auth/api/authApi'
import { AuthContext } from '@/features/auth/model/AuthContext'
import type { AuthStatus } from '@/features/auth/model/AuthContext'
import { authStorage } from '@/features/auth/model/authStorage'

interface AuthProviderProps {
  children: ReactNode
}

interface AuthState {
  status: AuthStatus
  userId: string | null
}

export function AuthProvider({ children }: AuthProviderProps) {
  const queryClient = useQueryClient()
  const [authState, setAuthState] = useState<AuthState>(() => {
    const storedUserId = authStorage.getUserId()

    return storedUserId
      ? { status: 'checking', userId: null }
      : { status: 'anonymous', userId: null }
  })

  useEffect(() => {
    const storedUserId = authStorage.getUserId()

    if (!storedUserId) {
      return
    }

    const userIdToRestore = storedUserId
    const abortController = new AbortController()

    async function restoreAuthentication() {
      try {
        const user = await findUser(userIdToRestore, abortController.signal)
        authStorage.setUserId(user.userId)
        setAuthState({ status: 'authenticated', userId: user.userId })
      } catch (error) {
        if (error instanceof DOMException && error.name === 'AbortError') {
          return
        }

        authStorage.removeUserId()
        queryClient.clear()
        setAuthState({ status: 'anonymous', userId: null })
      }
    }

    void restoreAuthentication()

    return () => abortController.abort()
  }, [queryClient])

  const login = useCallback(
    async (userId: string) => {
      const user = await findUser(userId)

      queryClient.clear()
      authStorage.setUserId(user.userId)
      setAuthState({ status: 'authenticated', userId: user.userId })
    },
    [queryClient],
  )

  const logout = useCallback(() => {
    authStorage.removeUserId()
    queryClient.clear()
    setAuthState({ status: 'anonymous', userId: null })
  }, [queryClient])

  const contextValue = useMemo(
    () => ({
      status: authState.status,
      userId: authState.userId,
      login,
      logout,
    }),
    [authState, login, logout],
  )

  return <AuthContext value={contextValue}>{children}</AuthContext>
}
