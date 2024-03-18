<template>
  <v-navigation-drawer expand-on-hover rail permanent v-if="user?.token" class="elevation-5">
    <v-list>
      <v-list-item :title="useUserStore().user?.email?.split('@')[0]">
        <template v-slot:prepend>
          <v-avatar color="grey" rounded="1" class="">
            <v-img cover src="../../assets/icon.svg">
              <template v-slot:placeholder>
                <div class="d-flex align-center justify-center fill-height  bg-white">
                  <v-progress-circular color="#00ADEF" indeterminate></v-progress-circular>
                </div>
              </template>
            </v-img>
          </v-avatar>
        </template>
        <v-list-item-subtitle @click.stop="logout" style="cursor: pointer;">
          <v-btn size="small" prepend-icon="mdi-logout-variant" variant="text" density="compact" color="#000000DE"
            class="pa-0 pl-1 ma-0">
            Đăng xuất
          </v-btn>
          <!-- <v-icon size="small">mdi-logout-variant</v-icon>
                  <span class="pl-1 text-body-2">Đăng xuất</span> -->
        </v-list-item-subtitle>
      </v-list-item>
    </v-list>

    <v-divider thickness="2" class="border-opacity-50"></v-divider>

    <v-list density="compact" v-model:selected="currentTab" nav color="#00ADEF" mandatory>
      <v-list-item prepend-icon="mdi-home-outline" title="Trang chủ" value="home"
        @click="router.push({ name: 'Home' })"></v-list-item>
      <v-list-item v-if="useUserStore().user?.role === 'admin1'" prepend-icon="mdi-account-group-outline" title="Quản lý tài khoản ADMIN 2" value="admin2management"
        @click="router.push({ name: 'Admin2Management' })"></v-list-item>
      <v-divider thickness="2" class="border-opacity-25 mb-2"></v-divider>
      <v-list-item @click="router.push({ name: 'AccountSettings' })" prepend-icon="mdi-account-cog-outline"
        title="Thiết lập tài khoản" value="setting"></v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>

<script setup>
import AuthService from '../../services/auth.service';
import { ref, onBeforeMount, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../store/user'
import { storeToRefs } from 'pinia'

const router = useRouter()
const route = useRoute()
// console.log(route.fullPath)
const userStore = useUserStore()
const { user } = storeToRefs(userStore)

onBeforeMount(() => {
  if (!user.value?.token) {
    router.push({ name: 'Login' })
  }
})

function getCurrentTab() {
  if (route.fullPath === '/quan-ly-tai-khoan-admin-2')
    return ['admin2management']
  else if (route.fullPath === '/')
    return ['home']
  else if (route.fullPath === '/thiet-lap-tai-khoan')
    return ['setting']
  else return []
}

const currentTab = ref([])
currentTab.value = getCurrentTab()

watch(route, () => {
  currentTab.value = getCurrentTab()
}, { deep: true })

async function logout() {
  try {
    const res = await AuthService.logout()
    userStore.reset()
    window.localStorage.clear()
    // console.log(res)
    // window.location.reload()
  } catch (error) {
    console.log(error)
  }
}

</script>

<style>
.collections .v-list-group__items {
  max-height: 475px !important;
  overflow-y: scroll !important;
}

.collections .v-list-group__items .v-list-item {
  padding-inline-start: 0 !important;
}
</style>