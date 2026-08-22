import { Navigate, useLocation, useNavigate } from 'react-router'
import { useAuth } from '@/features/auth/model/useAuth'
import { LoginForm } from '@/features/auth/ui/LoginForm'
import { LoadingState } from '@/shared/components/status'
import './LoginPage.css'

interface LoginLocationState {
  from?: string
}

export function LoginPage() {
  const { status } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const redirectPath = getRedirectPath(location.state)

  if (status === 'checking') {
    return (
      <main className="login-page">
        <div className="login-page__card">
          <LoadingState message="로그인 상태를 확인하고 있습니다." />
        </div>
      </main>
    )
  }

  if (status === 'authenticated') {
    return <Navigate to={redirectPath} replace />
  }

  return (
    <main className="login-page">
      <section className="login-page__card" aria-labelledby="login-title">
        <div className="login-page__heading">
          <p className="login-page__brand">Auto Retro</p>
          <h1 id="login-title">사용자 확인</h1>
          <p>DB에 등록된 사용자 ID를 입력해 주세요.</p>
        </div>

        <LoginForm onSuccess={() => navigate(redirectPath, { replace: true })} />
      </section>
    </main>
  )
}

function getRedirectPath(locationState: unknown) {
  const from = (locationState as LoginLocationState | null)?.from

  if (!from || !from.startsWith('/') || from.startsWith('//') || from === '/login') {
    return '/'
  }

  return from
}
