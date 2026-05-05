import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8000/api/v1'

export const api = axios.create({
  baseURL: API_URL,
})

let refreshPromise: Promise<string | null> | null = null

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('helpdesk_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config
    if (error.response?.status !== 401 || original?._retry || original?.headers?.skipAuthRefresh) {
      return Promise.reject(error)
    }

    const refreshToken = localStorage.getItem('helpdesk_refresh_token')
    if (!refreshToken) {
      localStorage.removeItem('helpdesk_token')
      localStorage.removeItem('helpdesk_refresh_token')
      return Promise.reject(error)
    }

    original._retry = true

    if (!refreshPromise) {
      refreshPromise = axios
        .post(`${API_URL}/auth/refresh`, { refresh_token: refreshToken })
        .then((response) => {
          const nextAccess = response.data.access_token as string
          const nextRefresh = response.data.refresh_token as string
          localStorage.setItem('helpdesk_token', nextAccess)
          localStorage.setItem('helpdesk_refresh_token', nextRefresh)
          return nextAccess
        })
        .catch(() => {
          localStorage.removeItem('helpdesk_token')
          localStorage.removeItem('helpdesk_refresh_token')
          return null
        })
        .finally(() => {
          refreshPromise = null
        })
    }

    const newAccess = await refreshPromise
    if (!newAccess) return Promise.reject(error)
    original.headers.Authorization = `Bearer ${newAccess}`
    return api(original)
  },
)
