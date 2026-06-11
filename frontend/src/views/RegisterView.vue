<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'

const email = ref('')
const password = ref('')
const error = ref('')
const router = useRouter()
const auth = useAuthStore()

// A04-04 : règles de complexité du mot de passe (alignées avec @StrongPassword côté backend)
const passwordRules = computed(() => ({
  length: password.value.length >= 12,
  uppercase: /[A-Z]/.test(password.value),
  lowercase: /[a-z]/.test(password.value),
  digit: /[0-9]/.test(password.value),
  special: /[^A-Za-z0-9]/.test(password.value)
}))

const isPasswordValid = computed(() => Object.values(passwordRules.value).every(Boolean))

async function register() {
  try {
    error.value = ''
    if (!isPasswordValid.value) {
      error.value = 'Le mot de passe ne respecte pas les critères de sécurité'
      return
    }
    const { data } = await api.post('/auth/register', {
      email: email.value,
      password: password.value
    })
    auth.setToken(data.token, data.user)
    router.push('/')
  } catch (e) {
    error.value = e.response?.data?.error || 'Erreur lors de l\'inscription'
  }
}
</script>

<template>
  <div class="row justify-content-center">
    <div class="col-md-6">
      <div class="card">
        <div class="card-body">
          <h2 class="card-title text-center mb-4">Inscription</h2>
          <div v-if="error" class="alert alert-danger">{{ error }}</div>
          <form @submit.prevent="register">
            <div class="mb-3">
              <label class="form-label">Email</label>
              <input v-model="email" type="email" class="form-control" required>
            </div>
            <div class="mb-3">
              <label class="form-label">Mot de passe</label>
              <!-- A04-04 : retour visuel sur la complexité du mot de passe -->
              <input v-model="password" type="password" class="form-control" minlength="12" required>
              <ul class="form-text small mt-2 ps-3 mb-0">
                <li :class="passwordRules.length ? 'text-success' : 'text-danger'">Au moins 12 caractères</li>
                <li :class="passwordRules.uppercase ? 'text-success' : 'text-danger'">Une majuscule</li>
                <li :class="passwordRules.lowercase ? 'text-success' : 'text-danger'">Une minuscule</li>
                <li :class="passwordRules.digit ? 'text-success' : 'text-danger'">Un chiffre</li>
                <li :class="passwordRules.special ? 'text-success' : 'text-danger'">Un caractère spécial</li>
              </ul>
            </div>
            <button type="submit" class="btn btn-success w-100">S'inscrire</button>
          </form>
          <p class="text-center mt-3">
            Déjà un compte ? <router-link to="/login">Se connecter</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>
