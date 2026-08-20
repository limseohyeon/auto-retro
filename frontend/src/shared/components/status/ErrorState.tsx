import './StatusState.css'

interface ErrorStateProps {
  title?: string
  message?: string
  onRetry?: () => void
}

export function ErrorState({
  title = '문제가 발생했습니다.',
  message = '잠시 후 다시 시도해 주세요.',
  onRetry,
}: ErrorStateProps) {
  return (
    <div className="status-state status-state--error" role="alert">
      <h2 className="status-state__title">{title}</h2>
      <p className="status-state__message">{message}</p>

      {onRetry && (
        <button className="status-state__retry-button" type="button" onClick={onRetry}>
          다시 시도
        </button>
      )}
    </div>
  )
}
