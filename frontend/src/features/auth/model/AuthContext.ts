import { createContext } from 'react'

export type AuthStatus = 'checking' | 'authenticated' | 'anonymous'

export interface AuthContextValue {
  status: AuthStatus
  userId: string | null
  login: (userId: string) => Promise<void>
  logout: () => void
}

export const AuthContext = createContext<AuthContextValue | null>(null)
