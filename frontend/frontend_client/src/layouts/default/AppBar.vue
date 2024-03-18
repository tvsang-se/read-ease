<template>
  <v-navigation-drawer expand-on-hover rail permanent v-if="user?.token">
    <v-list>
      <v-list-item  :title="useUserStore().user?.email?.split('@')[0]">
        <template v-slot:prepend>
          <v-avatar color="grey" rounded="1" class="">
            <!-- <v-img cover :src="user.avatar">
              <template v-slot:placeholder>
                <div class="d-flex align-center justify-center fill-height  bg-white">
                  <v-progress-circular color="#00ADEF" indeterminate></v-progress-circular>
                </div>
              </template>
            </v-img> -->
            <img crossorigin="anonymous" style="object-fit: cover; max-width: 100%" :src="user.avatar"/>
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
      <v-list-item prepend-icon="mdi-library-outline" title="Thư viện" value="library"
        @click="router.push({ name: 'Home' })"></v-list-item>
      <v-list-item prepend-icon="mdi-bookshelf" title="Bộ sưu tập" value="collections"
        @click="router.push('/bo-suu-tap/0')"></v-list-item>
      <v-divider thickness="2" class="border-opacity-25 mb-2"></v-divider>
      <v-list-item @click="router.push({ name: 'AccountSettings' })" prepend-icon="mdi-account-cog-outline"
        title="Thiết lập tài khoản" value="setting"></v-list-item>
      <!-- <v-list-group value="collections" class="collections">
        <template v-slot:activator="{ props }">
          <v-list-item v-bind="props" prepend-icon="mdi-bookshelf" title="Bộ sưu tập"></v-list-item>
        </template>
        <v-tooltip location="top" v-for="(collection, i) in collections">
          <template v-slot:activator="{ props }">
            <v-list-item v-bind="props" :value="collection.name" :key="i">
              <v-list-item-title>
                {{ collection.name }} <span class="pl-1">({{ collection.quantity }})</span>
              </v-list-item-title>
            </v-list-item>
          </template>
          <span>{{ collection.name }} ({{ collection.quantity }})</span>
        </v-tooltip>
      </v-list-group> -->
    </v-list>
  </v-navigation-drawer>
</template>

<script setup>
import AuthService from '../../services/auth.service';
import FileService from '../../services/file.service';
import { ref, onBeforeMount, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../store/user'
import { storeToRefs } from 'pinia'
import DocumentList from '@/components/DocumentList.vue';

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
  if (route.fullPath.includes('bo-suu-tap'))
    return ['collections']
  else if (route.fullPath === '/')
    return ['library']
  else if (route.fullPath === '/thiet-lap-tai-khoan')
    return ['setting']
  else return []
}

const currentTab = ref([])
currentTab.value = getCurrentTab()

watch(route, () => {
  currentTab.value = getCurrentTab()
}, { deep: true })

const collections = ref([])

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

// function detectTabClose() {
//   window.addEventListener("beforeunload", async (e) => {
//     var confirmationmessage = "o/";
//     e.returnvalue = confirmationmessage;

//     userStore.reset()
//     await logout()
//     // console.log('detectTabClose')

//     return confirmationmessage;
//   });
// }
// detectTabClose()


</script>

<!-- <script>
  export default {
    created() {

    }
  }
</script> -->

<style>
.collections .v-list-group__items {
  max-height: 475px !important;
  overflow-y: scroll !important;
}

.collections .v-list-group__items .v-list-item {
  padding-inline-start: 0 !important;
}
</style>
