import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { useAuth } from '@/features/auth/model/useAuth'
import { ApiError } from '@/shared/api/ApiError'
import './LoginForm.css'

const loginSchema = z.object({
  userNm: z
    .string()
    .trim()
    .min(1, '사용자 이름을 입력해 주세요.')
    .max(100, '사용자 이름은 100자 이하여야 합니다.'),
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
      userNm: '',
    },
  })

  const submitLogin = handleSubmit(async ({ userNm }) => {
    try {
      await login(userNm)
      onSuccess()
    } catch (error) {
      if (error instanceof ApiError && (error.status === 400 || error.code === 'USER-001')) {
        setError('userNm', { message: error.message })
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
        <label className="login-form__label" htmlFor="userNm">
          사용자 이름
        </label>
        <input
          className="login-form__input"
          id="userNm"
          type="text"
          autoComplete="username"
          aria-invalid={Boolean(errors.userNm)}
          aria-describedby={errors.userNm ? 'userNm-error' : undefined}
          placeholder="예: seohyeon"
          {...register('userNm')}
        />
        {errors.userNm && (
          <p className="login-form__error" id="userNm-error" role="alert">
            {errors.userNm.message}
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
