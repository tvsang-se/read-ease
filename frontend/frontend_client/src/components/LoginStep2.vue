<template>
    <v-form @submit.prevent="submitFormPassword" v-model="formPassword">
        <v-text-field v-model.trim="password" :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'" class="mb-2"
            :type="visible ? 'text' : 'password'" label="Mật khẩu" placeholder="Nhập vào mật khẩu của bạn" autofocus
            variant="outlined" @click:append-inner="visible = !visible" :rules="[rules.required, rules.password]"
            maxLength="20" :error-messages="errorMessages" @input="removeEmailNotExistError"></v-text-field>
    </v-form>
    <br>

    <v-card-actions class="d-flex justify-space-between pa-0">
        <v-btn size="small" variant="text" class="text-body-1" @click="router.push({name: 'ForgotPassword'})">
            Quên mật khẩu ?
        </v-btn>
        <v-btn :disabled="!formPassword" size="large" variant="flat" @click="submitFormPassword">
            Tiếp tục
        </v-btn>
    </v-card-actions>
</template>

<script setup>
import { ref } from 'vue'
import { onBeforeRouteLeave, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useAuthStore } from '../store/auth'
import { useUserStore } from '../store/user'
import AuthService from '../services/auth.service';

const authStore = useAuthStore()
const userStore = useUserStore()
const { loading } = storeToRefs(authStore)
const { user } = storeToRefs(userStore)

const router = useRouter()
const formPassword = ref("")
const password = ref("Aa!12345") //Aa!12345
const visible = ref(false)
const errorMessages = ref([])
const rules = ref({
    required: value => !!value || 'Trường này là bắt buộc.',
    counter: value => value.length >= 8 || 'Mật khẩu không hợp lệ !',
    password: value => {
        // const pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{0,30}$/
        const pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?])[A-Za-z\d!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]{8,20}$/gm
        return pattern.test(value) || 'Mật khẩu không hợp lệ !'
        //'Phải có ít nhất 1 chữ cái viết hoa, 1 chữ thường, 1 chữ số và 1 ký tự đặc biệt.'
        // 'At least one uppercase letter, one lowercase letter, one number and one special character'
    },
})

async function submitFormPassword() {
    try {
        if (!formPassword.value) 
            return
        loading.value = true
        const res = await AuthService.loginStep2({email: authStore.email, password: password.value})
        // console.log(res.data)
        useUserStore().setInfoUser(res.data)
        setTimeout(() => {
            router.push({name: "Home"})
        },200)
        // setTimeout(() => {
        // },60)
        setTimeout(() => loading.value = false, 5000)
    } catch (error) {
        // console.warn(error)
        const status = error.response?.status
        
        if(status === 406)
            errorMessages.value.push("Mật khẩu không chính xác !")
        else if(status === 403) {
            alert("Tài khoản này đã được đăng nhập trên máy khác !")
        }
            console.warn(error)
            loading.value = false
    } finally {
        
    }
    // console.log("updateInfoUserStep2: ", { email: email.value, password })

    // setTimeout(() => {
    //     loading.value = false
    //     if (password.value === 'nlnKTPM1@')
    //         alert(`Đăng nhập thành công: ${authStore.email}`)
    //     else
    //         errorMessages.value.push("Mật khẩu không chính xác !")

    // }, 2000)
}

function removeEmailNotExistError() {
    errorMessages.value = []
}

onBeforeRouteLeave((to, from) => {
    // if (password.value.length === 0 && authStore.email)
    //     return true
    // const answer = window.confirm(
    //     'Dữ liệu đã nhập sẽ mất nếu bạn rời đi ?'
    // )
    // // cancel the navigation and stay on the same page
    // if (!answer) {
    //     return false
    // } else {
    //     authStore.$reset()
    //     return true
    // }
    // if(to.name !== "ForgotPassword")
    //     authStore.reset()
})
</script>