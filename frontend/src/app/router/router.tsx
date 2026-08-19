import { createBrowserRouter } from 'react-router'
import App from '@/app/App'
import { DevRecordListPage } from '@/pages/dev-record/DevRecordListPage'
import { HomePage } from '@/pages/home/HomePage'
import { NotFoundPage } from '@/pages/not-found/NotFoundPage'
import { PresentationListPage } from '@/pages/presentation/PresentationListPage'
import { WeeklyReportListPage } from '@/pages/weekly-report/WeeklyReportListPage'

export const router = createBrowserRouter([
  {
    element: <App />,
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
])
