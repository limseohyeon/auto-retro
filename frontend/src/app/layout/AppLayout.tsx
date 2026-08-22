import { Link, NavLink, Outlet, useNavigate } from 'react-router'
import { useAuth } from '@/features/auth/model/useAuth'
import './AppLayout.css'

const navigationItems = [
  { to: '/records', label: '개발 기록' },
  { to: '/reports', label: '주간 리포트' },
  { to: '/presentations', label: '발표 초안' },
]

export function AppLayout() {
  const { userId, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <div className="app-layout">
      <header className="app-header">
        <div className="app-header__inner">
          <Link className="app-logo" to="/">
            Auto Retro
          </Link>

          <nav className="app-navigation" aria-label="주요 메뉴">
            {navigationItems.map((item) => (
              <NavLink
                className={({ isActive }) =>
                  isActive ? 'app-navigation__link active' : 'app-navigation__link'
                }
                key={item.to}
                to={item.to}
              >
                {item.label}
              </NavLink>
            ))}
          </nav>

          <div className="app-user">
            <span className="app-user__id">사용자 {userId}</span>
            <button className="app-user__logout" type="button" onClick={handleLogout}>
              로그아웃
            </button>
          </div>
        </div>
      </header>

      <main className="app-content">
        <Outlet />
      </main>
    </div>
  )
}
