<template >
    <v-sheet class="w-100">
        <v-row align-content="center" justify="center"  class="mr-0">
            <v-col md="5" sm="8" xs="12" class="pa-0 pa-sm-4">
                <v-card class="pa-6 pa-md-8" :elevation="elevationOfVCard" min-height="473.187">
                    <v-progress-linear :active="loading" :indeterminate="loading" absolute bottom
                        color="#00ADEF"></v-progress-linear>

                    <v-img class="mx-auto mb-2 login-signup-logo" src="../assets/logo_vietnamese.svg" max-width="240px"
                        width="" min-width="200px" @click="router.push({ name: 'Home' })"></v-img>

                    <v-card-item class="d-flex justify-center align-center mb-4">
                        <v-card-title v-if="inStep1" class="text-md-h4 text-sm-h5 text-center pb-2">Đăng nhập</v-card-title>
                        <div v-else>
                            <span class="text-email test-hi">Xin chào</span>
                            <v-btn size="x-small" rounded variant="outlined" class=" text-center pb-5"
                                @click="inStep1 = true">
                                <span class="text-email"> {{ email.length > 20 ? `${email.slice(0, 19)}..` : email }}
                                </span>
                            </v-btn>
                        </div>
                    </v-card-item>

                    <LoginStep1 v-if="inStep1">
                    </LoginStep1>

                    <LoginStep2 v-else ></LoginStep2>

                    <v-overlay v-model="loading" contained persistent />
                </v-card>
            </v-col>
        </v-row>
    </v-sheet>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import LoginStep1 from '../components/LoginStep1.vue';
import LoginStep2 from '../components/LoginStep2.vue';
import { useDisplay } from 'vuetify'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useAuthStore } from '../store/auth'

const authStore = useAuthStore()
const { email, loading, inStep1 } = storeToRefs(authStore)

const router = useRouter()
const { smAndUp } = useDisplay()
const elevationOfVCard = computed(() => {
    // console.log('elevationOfVCard: ', (smAndUp.value) ? 2 : 0)
    return (smAndUp.value) ? 2 : 0
})
// const email = ref("")
// const loading = ref(false)
// const inStep1 = computed(() => {
//     if(email.value==="")
//         return true
//     return false
// })
// const inStep1 = (email.value.length==0)?ref(true):ref(false)
// watch(email, () => {
//     if(email.value.length==0)
//         inStep1.value = true
//     else   
//         inStep1.value = false
// })

</script>

<style scoped>
@media (min-width: 960px){
    .text-md-h4 {
        font-size: 28px !important;
    }
}
.text-email {
    font-size: 15px;
    color: rgb(72, 71, 71) !important;
    margin-bottom: -1px;
    font-weight: 700;
}

.test-hi {
    display: block;
    font-size: 15px;
    font-weight: 500;
    margin-bottom: -4.3px;
    text-align: center;
}
</style>