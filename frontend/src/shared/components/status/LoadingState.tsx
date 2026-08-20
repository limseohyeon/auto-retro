import './StatusState.css'

interface LoadingStateProps {
  message?: string
}

export function LoadingState({ message = '데이터를 불러오는 중입니다.' }: LoadingStateProps) {
  return (
    <div className="status-state" role="status" aria-live="polite">
      <span className="status-state__spinner" aria-hidden="true" />
      <p className="status-state__message">{message}</p>
    </div>
  )
}
