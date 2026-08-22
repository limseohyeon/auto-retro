const USER_ID_STORAGE_KEY = 'auto-retro:user-id'

export const authStorage = {
  getUserId() {
    return localStorage.getItem(USER_ID_STORAGE_KEY)
  },

  setUserId(userId: string) {
    localStorage.setItem(USER_ID_STORAGE_KEY, userId)
  },

  removeUserId() {
    localStorage.removeItem(USER_ID_STORAGE_KEY)
  },
}
