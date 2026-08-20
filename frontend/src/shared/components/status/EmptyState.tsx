import type { ReactNode } from 'react'
import './StatusState.css'

interface EmptyStateProps {
  title: string
  description?: string
  action?: ReactNode
}

export function EmptyState({ title, description, action }: EmptyStateProps) {
  return (
    <div className="status-state status-state--empty">
      <h2 className="status-state__title">{title}</h2>

      {description && <p className="status-state__message">{description}</p>}

      {action && <div className="status-state__action">{action}</div>}
    </div>
  )
}
