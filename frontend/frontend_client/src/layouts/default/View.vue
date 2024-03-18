<template>
    <v-main>
        <!-- <v-alert :type="typeAlert" :text="contentAlert" v-if="showAlert" :title="titleAlert" closable density="compact"
            max-width="400" position="absolute" location="top right" prominent >
            <template v-slot:close>
                <v-btn icon="mdi-close-outline" @click="timeAlert = 0" color="white"></v-btn>
            </template>
        </v-alert> -->
        <!-- :key="$route.fullPath" -->
        <router-view/>
    </v-main>
</template>

<script setup>
import { ref, watch, onBeforeUnmount } from 'vue';
import { storeToRefs } from 'pinia'
import { useAlertStore } from '../../store/alert'

const alertStore = useAlertStore()
const { typeAlert, titleAlert, contentAlert, timeAlert } = storeToRefs(alertStore)
const showAlert = ref(false)

watch(timeAlert, () => {
    if (timeAlert.value > 0 && typeAlert.value !== "") {
        showAlert.value = true
        // console.log(`show alert ${typeAlert.value} ${contentAlert.value} ${timeAlert.value}`)
        setTimeout(() => { showAlert.value = false, alertStore.resetAlert() }, timeAlert.value)
    } else if (timeAlert.value === 0) {
        showAlert.value = 0
    }
})

onBeforeUnmount(() => {
    if (timeAlert.value !== 0) {
        alertStore.resetAlert()
    }
})
</script>
