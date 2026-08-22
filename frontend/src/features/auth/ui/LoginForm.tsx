import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { useAuth } from '@/features/auth/model/useAuth'
import { ApiError } from '@/shared/api/ApiError'
import './LoginForm.css'

const loginSchema = z.object({
  userId: z
    .string()
    .trim()
    .min(1, '사용자 ID를 입력해 주세요.')
    .regex(/^[1-9]\d*$/, '사용자 ID는 1 이상의 정수여야 합니다.'),
})

type LoginFormValues = z.infer<typeof loginSchema>

interface LoginFormProps {
  onSuccess: () => void
}

export function LoginForm({ onSuccess }: LoginFormProps) {
  const { login } = useAuth()
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      userId: '',
    },
  })

  const submitLogin = handleSubmit(async ({ userId }) => {
    try {
      await login(userId)
      onSuccess()
    } catch (error) {
      if (error instanceof ApiError && (error.status === 400 || error.code === 'USER-001')) {
        setError('userId', { message: error.message })
        return
      }

      setError('root', {
        message: error instanceof Error ? error.message : '로그인 중 오류가 발생했습니다.',
      })
    }
  })

  return (
    <form className="login-form" onSubmit={submitLogin} noValidate>
      <div className="login-form__field">
        <label className="login-form__label" htmlFor="userId">
          사용자 ID
        </label>
        <input
          className="login-form__input"
          id="userId"
          type="text"
          inputMode="numeric"
          autoComplete="username"
          aria-invalid={Boolean(errors.userId)}
          aria-describedby={errors.userId ? 'userId-error' : undefined}
          placeholder="예: 1"
          {...register('userId')}
        />
        {errors.userId && (
          <p className="login-form__error" id="userId-error" role="alert">
            {errors.userId.message}
          </p>
        )}
      </div>

      {errors.root && (
        <p className="login-form__error" role="alert">
          {errors.root.message}
        </p>
      )}

      <button className="login-form__submit" type="submit" disabled={isSubmitting}>
        {isSubmitting ? '확인 중...' : '로그인'}
      </button>
    </form>
  )
}
