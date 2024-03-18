<template>
  <!-- <blank>
    <router-view v-slot="{ Component }">
      <transition name="scale-slide">
        <component :is="Component" />
      </transition>
    </router-view>
  </blank> -->
  <v-alert :type="typeAlert" :text="contentAlert" v-if="timeAlert > 0" :title="titleAlert" closable density="compact"
    max-width="400" position="absolute" location="top right">
    <template v-slot:close>
      <v-btn icon="mdi-close-outline" @click="timeAlert = 0" color="white"></v-btn>
    </template>
    <!-- close-icon="mdi-close-outline" -->
  </v-alert>
  <router-view></router-view>
</template>

<script setup>
import blank from './layouts/blank.vue';
import { ref, watch, onBeforeUnmount } from 'vue';
import { storeToRefs } from 'pinia'
import { useAlertStore } from './store/alert'

const alertStore = useAlertStore()
const { typeAlert, titleAlert, contentAlert, timeAlert } = storeToRefs(alertStore)

// const showAlert = ref(false)

// watch(timeAlert, () => {
//   if (timeAlert.value > 0 && typeAlert.value !== "") {
//     showAlert.value = true
//     // console.log(`show alert ${typeAlert.value} ${contentAlert.value} ${timeAlert.value}`)
//     setTimeout(() => { showAlert.value = false, alertStore.resetAlert() }, timeAlert.value)
//   } else if (timeAlert.value === 0) {
//     showAlert.value = 0
//   }
// })

// onBeforeUnmount(() => {
//   if (timeAlert.value !== 0) {
//     alertStore.resetAlert()
//   }
// })
</script>

<style>
.v-alert {
    margin-top: 55px;
    margin-right: 4px;
    z-index: 11111;
}

.v-alert__content {
    font-size: 15px !important;
    text-align: justify !important;
}

.scale-slide-enter-active,
.scale-slide-leave-active {
  position: absolute;
  transition: all 0.3s ease;
}

.scale-slide-enter-from {
  left: -100%;
}

.scale-slide-enter-to {
  left: 0%;
}

.scale-slide-leave-from {
  transform: scale(1);
}

.scale-slide-leave-to {
  transform: scale(0.4);
}
</style>