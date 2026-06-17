<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import DOMPurify from 'dompurify'
import api from '@/services/api'

const route = useRoute()
const user = ref({})
const projects = ref([])

const sanitizedBio = computed(() => DOMPurify.sanitize(user.value.bio || ''))

onMounted(async () => {
  // CORRIGÉ A01-04 : utilisation de l'UUID à la place de l'ID numérique
  const userUuid = route.params.uuid
  const { data: userData } = await api.get(`/users/profile/${userUuid}`)
  user.value = userData

  const { data: projectData } = await api.get('/projects')
  projects.value = projectData.filter(p => p.ownerId == userData.id)
})

</script>

<template>
  <div class="profile">
    <div class="card mb-4">
      <div class="card-body">
        <div class="d-flex align-items-center mb-3">
          <img v-if="user.avatarUrl" :src="user.avatarUrl" class="rounded-circle me-3"
               width="80" height="80" alt="Avatar">
          <div>
            <h2>{{ user.email }}</h2>
            <span class="badge bg-secondary">{{ user.role }}</span>
          </div>
        </div>

        <!-- A03-02 : bio sanitisée avec DOMPurify avant injection dans le DOM -->
        <div class="bio" v-html="sanitizedBio"></div>
      </div>
    </div>

    <h3>Projets</h3>
    <div class="row">
      <div v-for="project in projects" :key="project.id" class="col-md-4 mb-3">
        <div class="card">
          <div class="card-body">
            <h5>{{ project.title }}</h5>
            <p>{{ project.description }}</p>
            <router-link :to="`/project/${project.id}`" class="btn btn-sm btn-primary">
              Voir
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
