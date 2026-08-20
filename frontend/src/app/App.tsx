import { AppLayout } from '@/app/layout/AppLayout'
import { AppProviders } from '@/app/providers/AppProviders'

function App() {
  return (
    <AppProviders>
      <AppLayout />
    </AppProviders>
  )
}

export default App
