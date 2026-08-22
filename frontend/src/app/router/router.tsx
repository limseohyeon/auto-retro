import { createBrowserRouter } from 'react-router'
import App from '@/app/App'
import { AppLayout } from '@/app/layout/AppLayout'
import { ProtectedRoute } from '@/app/router/ProtectedRoute'
import { DevRecordListPage } from '@/pages/dev-record/DevRecordListPage'
import { HomePage } from '@/pages/home/HomePage'
import { LoginPage } from '@/pages/login/LoginPage'
import { NotFoundPage } from '@/pages/not-found/NotFoundPage'
import { PresentationListPage } from '@/pages/presentation/PresentationListPage'
import { WeeklyReportListPage } from '@/pages/weekly-report/WeeklyReportListPage'

export const router = createBrowserRouter([
  {
    element: <App />,
    children: [
      {
        path: 'login',
        element: <LoginPage />,
      },
      {
        element: <ProtectedRoute />,
        children: [
          {
            element: <AppLayout />,
            children: [
              {
                index: true,
                element: <HomePage />,
              },
              {
                path: 'records',
                element: <DevRecordListPage />,
              },
              {
                path: 'reports',
                element: <WeeklyReportListPage />,
              },
              {
                path: 'presentations',
                element: <PresentationListPage />,
              },
              {
                path: '*',
                element: <NotFoundPage />,
              },
            ],
          },
        ],
      },
    ],
  },
])
