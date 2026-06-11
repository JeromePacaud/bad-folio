import axios from 'axios'

// 🔴 : pas de validation HTTPS — communication en clair
const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// 🔴 : token exposé dans chaque requête sans vérification
api.interceptors.request.use(config => {
  const token = localStorage.getItem('devfolio_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default api
