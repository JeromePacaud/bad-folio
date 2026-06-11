import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: () => import('@/views/HomeView.vue') },
    { path: '/login', component: () => import('@/views/LoginView.vue') },
    { path: '/register', component: () => import('@/views/RegisterView.vue') },
    { path: '/profile/:id', component: () => import('@/views/ProfileView.vue') },
    { path: '/project/:id', component: () => import('@/views/ProjectView.vue') },

    // 🔴 A01-06 : "protection" admin uniquement côté client
    // Protection de la route /admin côté client (confort uniquement, pas de sécurité)
// La vraie vérification du rôle se fait côté back dans SecurityConfig.java
    {
      path: '/admin',
      component: () => import('@/views/AdminView.vue'),
      beforeEnter: (to, from, next) => {

        // Récupère le token JWT stocké lors de la connexion
        const token = localStorage.getItem('devfolio_token')

        // Si pas de token, l'utilisateur n'est pas connecté
        if (!token) {
          return next('/login')
        }

        // Un token JWT est composé de 3 parties séparées par des points
        // La deuxième partie contient les données de l'utilisateur
        const parts = token.split(".")
        const payload = parts[1]

        // Décode la deuxième partie (encodée en base64)
        const decoded = atob(payload)

        // Transforme la string JSON en objet JavaScript
        const user = JSON.parse(decoded)

        // Vérifie si l'utilisateur a le rôle ADMIN
        if (user && user.role === 'ADMIN') {
          next() // autorise l'accès
        } else {
          next('/login') // redirige vers la page de connexion
        }
      }
    }
  ]
})

export default router
