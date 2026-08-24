const USER_NAME_STORAGE_KEY = 'auto-retro:user-name'
const LEGACY_USER_ID_STORAGE_KEY = 'auto-retro:user-id'

export const authStorage = {
  getUserNm() {
    return localStorage.getItem(USER_NAME_STORAGE_KEY)
  },

  setUserNm(userNm: string) {
    localStorage.setItem(USER_NAME_STORAGE_KEY, userNm)
    localStorage.removeItem(LEGACY_USER_ID_STORAGE_KEY)
  },

  removeUserNm() {
    localStorage.removeItem(USER_NAME_STORAGE_KEY)
    localStorage.removeItem(LEGACY_USER_ID_STORAGE_KEY)
  },
}
