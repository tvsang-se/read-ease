<template>
  <div v-if="!userStore.user.email">
    <v-btn @click="router.push({ name: 'Login' })">LOGIN</v-btn>
    <v-btn @click="router.push({ name: 'Signup' })">SIGNUP</v-btn>
    <!-- <v-btn @click="router.push({ name: 'ReadFile1' })">ReadFile1</v-btn> -->
  </div>
  <div v-else>
    <v-btn @click="router.push({ name: 'ReadFile' })">ReadFile</v-btn>
    <v-btn @click="getAccessTokenGoogle">GetAccessTokenGoogle</v-btn>
    <v-btn @click="logout">LOGOUT</v-btn>

  </div>
  <!-- <v-btn @click="router.push({name: 'ReadFile'})">ReadFile</v-btn> -->
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import AuthService from '../services/auth.service';
import FileService from '../services/file.service';

const userStore = useUserStore()
const router = useRouter()

async function logout() {
  try {
    const res = await AuthService.logout()
    // console.log(res)
    userStore.reset()
    window.location.reload()
  } catch (error) {
    console.log(error)
  }
}

async function getAccessTokenGoogle() {
    try {
        const res = await FileService.getGoogleAccessToken()
        console.log(res)
    } catch (error) {
        console.log(error)
    }
}
</script>
