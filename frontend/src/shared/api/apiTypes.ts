export interface ApiResponse<T> {
  success: true
  data: T
}

export interface FieldError {
  field: string
  message: string
}

export interface ApiErrorResponse {
  success: false
  status: number
  code: string
  message: string
  fieldErrors: FieldError[]
}
