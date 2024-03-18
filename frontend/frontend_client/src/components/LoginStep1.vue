<template>
    <v-form @submit.prevent="submitFormEmail" v-model="formEmail">
        <v-text-field v-model.trim="emailTemp" :rules="[rules.required, rules.email]" class="mb-2" clearable variant="outlined"
            :error-messages="errorMessages" label="Email" @input="removeEmailNotExistError"></v-text-field>
    </v-form>
    <br>

    <v-card-actions class="d-flex justify-space-between pa-0">
        <v-btn size="small" variant="text" class="text-body-1" @click="router.push({ name: 'Signup' })">
            Tạo tài khoản
        </v-btn>

        <v-btn :disabled="!formEmail" size="large" variant="flat" @click="submitFormEmail">
            Tiếp tục
        </v-btn>
    </v-card-actions>

    <div class="content my-3 mx-auto w-98">
        <p class="or">hoặc </p>
    </div>

    <div class="w-100 d-flex flex-row justify-center align-center">
        <v-btn block size="large" variant="outlined" @click="() => login()">
            <template v-slot:prepend>
                <v-icon>
                    <v-img src="../assets/google-icon.svg">
                    <!-- <v-img src="https://upload.wikimedia.org/wikipedia/commons/5/53/Google_%22G%22_Logo.svg"> -->
                    </v-img>
                </v-icon>
            </template>
            Tiếp tục với Google
        </v-btn>
    </div>
</template>

<script setup>
import { useOneTap, useTokenClient, revokeAccessToken, decodeCredential } from "vue3-google-signin";
import { ref, onBeforeMount } from 'vue'
import { useAlertStore } from '../store/alert'
import { useAuthStore } from '../store/auth'
import { useUserStore } from '../store/user'
import { storeToRefs } from 'pinia'
import { useRouter, onBeforeRouteLeave } from 'vue-router'
import AuthService from '../services/auth.service';

const router = useRouter()
const alertStore = useAlertStore()
const authStore = useAuthStore()
const { email, loading, inStep1 } = storeToRefs(authStore)

const formEmail = ref("")
const emailTemp = ref("")
const errorMessages = ref([])

onBeforeMount(() => {
    emailTemp.value = email.value
})

const rules = ref({
    required: value => !!value || 'Trường này là bắt buộc.',
    email: value => {
        const pattern =
            /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
        return pattern.test(value) || 'Email không hợp lệ.'
    },
})

async function submitFormEmail() {
    try {
        if (!formEmail.value) return
        const res = await AuthService.loginStep1({email: emailTemp.value})
        // const existedEmail = 
        // console.log(res)
        if(res.status === 200) {
            email.value = emailTemp.value
            inStep1.value = false
        }
    } catch (error) {
        const status = error.response.status
        if(status === 400)
            errorMessages.value.push("Email này chưa được liên kết với tài khoản nào !")
        else
            console.warn(error)
    } finally {
        loading.value = false
    }
}

// Call API
async function checkExistEmail() {
    if (!emailTemp.value) return

    // loading.value = true

    // await setTimeout(() => {
    //     loading.value = false
    //     if (email.value === 'nlnktpm@gmail.com')
    //         return true
    //     else
    //         return false
    // }, 1000)
    if (emailTemp.value === 'nlnktpm@gmail.com')
        return true
    else
        return false
}

function removeEmailNotExistError() {
    errorMessages.value = []
}

// Call API
async function handleContinueWithGoogle(emailGG, avatar) {
    try {
        loading.value = true
        let targetLanguage = navigator.language
        if(!targetLanguage.includes('zh') && targetLanguage.includes('-'))
            targetLanguage = targetLanguage.split('-')[0]
        const res = await AuthService.continueWithGoogle({email: emailGG, avatar, targetLanguage})
        useUserStore().setInfoUser(res)
        setTimeout(() => {
            router.push({name: "Home"})
        },200)
        setTimeout(() => loading.value = false, 10000)
        // console.log(res)
    } catch (error) {
        useAlertStore().showCommonError('')
        console.log(error)
        loading.value = false
    } 
}

useOneTap({
    context: 'use',
    onSuccess: (response) => {
        loading.value = true
        // console.log("Success:", response);
        const { email: emailGG, picture: avatar, email_verified } = decodeCredential(response.credential);
        if (!email_verified) {
            loading.value = false
            // showAlertFunc("error", "Email phải được xác thực trước khi truy cập vào hệ thống !")
            alertStore.setAlert("error", "", "Email phải được xác thực trước khi truy cập vào hệ thống !", 10000)
            return
        }
        handleContinueWithGoogle(emailGG, avatar)
    },
    onError: () => console.error("Error with One Tap Login"),
    // options
});

const handleOnSuccess = (response) => {
    loading.value = true
    // console.log("Access Token: ", response.access_token);
    //   userData.value = response.access_token
    fetch(import.meta.env.VITE_GOOGLE_OAUTH_API_GET_INFO_USER, {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${response.access_token}`
        }
    })
        .then((response) => {
            if (response.ok) {
                return response.json()
            } else {
                loading.value = false
                return Promise.reject({
                    status: response.status,
                    statusText: response.statusText
                })
            }
        })
        .then((data) => {
            const { email: emailGG, picture: avatar, verified_email } = data
            console.log(data)
            if (!verified_email) {
                loading.value = false
                alertStore.setAlert("error", "", "Email phải được xác thực trước khi truy cập vào hệ thống !", 10000)
                return
            }
            handleContinueWithGoogle(emailGG, avatar)
            revokeAccessToken(response.access_token, () => {
                loading.value = false
            })
        })
        .catch((error) => {
            console.log(error)
            loading.value = fase
        })
};

const handleOnError = (errorResponse) => {
    console.log("Error: ", errorResponse);
};

const { isReady, login } = useTokenClient({
    onSuccess: handleOnSuccess,
    onError: handleOnError,
    // other options
});

onBeforeRouteLeave((to, from) => {
    // if (emailTemp.value.length + email.value.length === 0)
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
    // authStore.reset()
})

</script>

<style scoped>
/* .or {
    text-align: center;
    font-size: 18px;
    background:
        linear-gradient(#000 0 0) left,
        linear-gradient(#000 0 0) right;
    background-size: 40% 1px;
    background-repeat: no-repeat;
} */

/* .v-btn {
    text-transform: none;
} */
</style>