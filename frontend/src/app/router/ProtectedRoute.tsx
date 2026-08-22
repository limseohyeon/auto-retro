import { Navigate, Outlet, useLocation } from 'react-router'
import { useAuth } from '@/features/auth/model/useAuth'
import { LoadingState } from '@/shared/components/status'
import './ProtectedRoute.css'

export function ProtectedRoute() {
  const { status } = useAuth()
  const location = useLocation()

  if (status === 'checking') {
    return (
      <main className="protected-route-status">
        <LoadingState message="사용자 정보를 확인하고 있습니다." />
      </main>
    )
  }

  if (status === 'anonymous') {
    const from = `${location.pathname}${location.search}${location.hash}`

    return <Navigate to="/login" replace state={{ from }} />
  }

  return <Outlet />
}
