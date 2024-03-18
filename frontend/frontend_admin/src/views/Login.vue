<template>
    <v-sheet class="w-100">
        <v-row align-content="center" justify="center" class="mr-0">
            <v-col md="5" sm="8" xs="12" class="pa-0 pa-sm-4">
                <v-card class="pa-6 pa-md-8" elevation="6">
                    <v-progress-linear :active="loading" :indeterminate="loading" absolute bottom
                        color="#00ADEF"></v-progress-linear>

                    <v-img class="mx-auto mb-2 login-signup-logo" src="../assets/logo_vietnamese.svg" max-width="240px"
                        width="" min-width="200px" @click="router.push({ name: 'Home' })"></v-img>

                    <v-card-item class="d-flex justify-center align-center mb-4">
                        <v-card-title class="text-md-h4 text-sm-h5 text-center pb-2">Đăng nhập - ADMIN</v-card-title>
                    </v-card-item>

                    <v-form @submit.prevent="submitFormLogin" v-model="formLogin">
                        <v-text-field v-model.trim="email" :rules="[rules.required, rules.email]" class="mb-3" clearable
                            variant="outlined" :error-messages="errorMessagesEmail" label="Email" @input="errorMessagesEmail = []">
                        </v-text-field>

                        <v-text-field v-model.trim="password" maxlength="20" counter pattern="/^\P{Mn}*$/g"
                            :append-inner-icon="visiblePassword ? 'mdi-eye-off' : 'mdi-eye'" class="mb-3"
                            :type="visiblePassword ? 'text' : 'password'" label="Mật khẩu"
                            :error-messages="errorMessagesPassword" placeholder="Nhập vào mật khẩu" variant="outlined" @input="errorMessagesPassword = []"
                            @click:append-inner="visiblePassword = !visiblePassword" id="password-field"
                            :rules="[rules.required, rules.password]">
                        </v-text-field>
                    </v-form>

                    <v-card-actions class="d-flex justify-end pa-0">
                        <v-btn :disabled="!formLogin" size="large" variant="flat" @click="submitFormLogin">
                            Đăng nhập
                        </v-btn>
                    </v-card-actions>

                    <v-overlay v-model="loading" contained persistent />
                </v-card>
            </v-col>
        </v-row>
    </v-sheet>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useDisplay } from 'vuetify'
import { useRouter, onBeforeRouteLeave } from 'vue-router'
import AuthService from '../services/auth.service';
import { useUserStore } from '../store/user'
import { useAlertStore } from '@/store/alert';

const router = useRouter()
// const { smAndUp } = useDisplay()
// const elevationOfVCard = computed(() => {
//     // console.log('elevationOfVCard: ', (smAndUp.value) ? 2 : 0)
//     return (smAndUp.value) ? 2 : 0
// })

const email = ref("readease.webapp@gmail.com") //readease.webapp@gmail.com
const password = ref("Aa!12345") //Aa!12345
const errorMessagesEmail = ref([])
const errorMessagesPassword = ref([])
const loading = ref(false)
const visiblePassword = ref(false)
const formLogin = ref(false)

const rules = ref({
    required: value => !!value || 'Trường này là bắt buộc.',
    counter: value => value.length >= 8 || 'Mật khẩu phải có ít nhất 8 ký tự.',
    email: value => {
        const pattern =
            /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
        return pattern.test(value) || 'Email không hợp lệ.'
    },
    password: value => {
        const pattern =
            /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?])[A-Za-z\d!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]{8,20}$/gm

        return pattern.test(value) || 'Mật khẩu không hợp lệ.'
    },
})

async function submitFormLogin() {
    try {
        if (!formLogin.value)
            return
        loading.value = true
        const res = await AuthService.login({ email: email.value, password: password.value })
        // const existedEmail = await checkExistEmail()
        // console.log(res.data)
        useUserStore().setInfoUser(res.data)
        setTimeout(() => { useAlertStore().resetAlert(); router.push({ name: "Home" }); }, 200)
        setTimeout(() => loading.value = false, 5000)
    } catch (error) {
        const status = error.response?.status
        if (status === 400)
            errorMessagesPassword.value.push("Mật khẩu không hợp lệ !")
        else if (status === 403)
            errorMessagesEmail.value.push("Email này không có quyền admin !")
        else if (status === 404)
            errorMessagesEmail.value.push("Email này chưa được liên kết với tài khoản nào !")
        // else
        //     console.warn(error)
        else useAlertStore().showCommonError('')
        console.log(error)
        loading.value = false
    }
}

function removeEmailNotExistError() {
    errorMessagesPassword.value = []
}

if(useUserStore().user?.token) {
    router.push({name: "Home"})
}

</script>

<style>
/* @media (min-width: 960px){
    .text-md-h4 {
        font-size: 28px !important;
    }
}
.password__details {
    font-size: 15px;
    font-weight: 400;
    vertical-align: center;
}

.password__details div.pl-sm-1>span {
    display: inline-flex;
    flex-direction: row;
    align-items: center;
}

.password__details div.pl-sm-1>span i {
    padding-right: 6px;
} */
</style>