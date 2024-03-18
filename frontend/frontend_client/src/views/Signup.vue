<template>
    <v-sheet class="w-100">
        <v-row align-content="center" justify="center"  class="mr-0">
            <v-col md="5" sm="8" xs="12" class="pa-0 pa-sm-4">
                <v-card class="pa-6 pa-md-8" :elevation="elevationOfVCard">
                    <v-progress-linear :active="loading" :indeterminate="loading" absolute bottom
                        color="#00ADEF"></v-progress-linear>

                    <v-img class="mx-auto mb-2 login-signup-logo" src="../assets/logo_vietnamese.svg" max-width="240px"
                        width="" min-width="200px" @click="router.push({ name: 'Home' })"></v-img>

                    <v-card-item class="d-flex justify-center align-center mb-4">
                        <v-card-title class="text-md-h4 text-sm-h5 text-center pb-2">Tạo tài khoản</v-card-title>
                    </v-card-item>

                    <v-form @submit.prevent="submitFormSignUp" v-model="formSignUp">
                        <v-text-field  v-model.trim="email" :rules="[rules.required, rules.email]" class="mb-3"
                            clearable variant="outlined" :error-messages="errorMessages" label="Email"
                            @input="removeEmailNotExistError">
                        </v-text-field>

                        <v-menu location="top" open-on-focus>
                            <template v-slot:activator="{ props }">
                                <v-text-field v-model.trim="password" maxlength="20" counter v-bind="props"
                                    pattern="/^\P{Mn}*$/g" :append-inner-icon="visiblePassword ? 'mdi-eye-off' : 'mdi-eye'"
                                    class="mb-3" :type="visiblePassword ? 'text' : 'password'" label="Mật khẩu"
                                    placeholder="Nhập vào mật khẩu" variant="outlined"
                                    @click:append-inner="visiblePassword = !visiblePassword" id="password-field"
                                    :rules="[rules.required, rules.password]">
                                </v-text-field>
                            </template>

                            <v-list class="password__details pa-3">
                                <span>Mật khẩu phải thoả mãn các tiêu chí sau:</span> <br>
                                <div class="pl-sm-1">
                                    <span>
                                        <v-icon v-if="password.length === 0" color="warning" icon="mdi-alert-circle"
                                            size="x-small"></v-icon>
                                        <v-icon v-else-if="conditionsOfPassword.length()" color="success"
                                            icon="mdi-check-circle" size="x-small"></v-icon>
                                        <v-icon v-else color="error" icon="mdi-close-circle" size="x-small"></v-icon>
                                        Có độ dài từ 8 đến 20 ký tự.
                                    </span> <br>
                                    <span>
                                        <v-icon v-if="password.length === 0" color="warning" icon="mdi-alert-circle"
                                            size="x-small"></v-icon>
                                        <v-icon v-else-if="conditionsOfPassword.onlyAlphabetNumberSpecialChar"
                                            color="success" icon="mdi-check-circle" size="x-small"></v-icon>
                                        <v-icon v-else color="error" icon="mdi-close-circle" size="x-small"></v-icon>
                                        Chỉ bao gồm chữ cái, chữ số, ký tự đặc biệt.
                                    </span> <br>
                                    <span>
                                        <v-icon v-if="password.length === 0" color="warning" icon="mdi-alert-circle"
                                            size="x-small"></v-icon>
                                        <v-icon v-else-if="conditionsOfPassword.atLeastOneUpperCaseLetterOneLowerCaseLetter"
                                            color="success" icon="mdi-check-circle" size="x-small"></v-icon>
                                        <v-icon v-else color="error" icon="mdi-close-circle" size="x-small"></v-icon>
                                        Ít nhất một chữ cái viết hoa, viết thường.
                                    </span> <br>
                                    <span>
                                        <v-icon v-if="password.length === 0" color="warning" icon="mdi-alert-circle"
                                            size="x-small"></v-icon>
                                        <v-icon v-else-if="conditionsOfPassword.atLeastOneNumber" color="success"
                                            icon="mdi-check-circle" size="x-small"></v-icon>
                                        <v-icon v-else color="error" icon="mdi-close-circle" size="x-small"></v-icon>
                                        Ít nhất một chữ số.
                                    </span> <br>
                                    <span>
                                        <v-icon v-if="password.length === 0" color="warning" icon="mdi-alert-circle"
                                            size="x-small"></v-icon>
                                        <v-icon v-else-if="conditionsOfPassword.atLeastOneSpecialChar" color="success"
                                            icon="mdi-check-circle" size="x-small"></v-icon>
                                        <v-icon v-else color="error" icon="mdi-close-circle" size="x-small"></v-icon>
                                        Ít nhất một ký tự đặc biệt.
                                    </span>
                                </div>
                            </v-list>
                        </v-menu>


                        <v-text-field v-model.trim="confirmPassword" maxlength="20"
                            :append-inner-icon="visibleConfirmPassword ? 'mdi-eye-off' : 'mdi-eye'" class="mb-3"
                            :type="visibleConfirmPassword ? 'text' : 'password'" label="Xác nhận mật khẩu"
                            placeholder="Nhập lại mật khẩu" variant="outlined"
                            @click:append-inner="visibleConfirmPassword = !visibleConfirmPassword"
                            :rules="[rules.sameValue]">
                        </v-text-field>
                    </v-form>

                    <div class="w-100  pa-0 ma-0 mb-4 mt-2 ">
                        <span class="d-flex flex-column justify-center align-center text-body-2 text-center">
                            <span>
                                Bằng việc tạo tài khoản, bạn đã đồng ý với <span class="font-weight-bold">ReasEase</span> về
                                <br>
                            </span>
                            <span>
                                <a href="#">Điều khoản dịch vụ</a>&nbsp; &
                                <a href="#">Chính sách bảo mật</a>
                            </span>
                        </span>
                    </div>

                    <v-card-actions class="d-flex justify-space-between pa-0">
                        <v-btn size="small" variant="text" class="text-body-1" @click="router.push({ name: 'Login' })">
                            Đăng nhập
                        </v-btn>

                        <v-btn :disabled="!formSignUp" size="large" variant="flat" @click="submitFormSignUp">
                            Tiếp tục
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

const router = useRouter()
const { smAndUp } = useDisplay()
const elevationOfVCard = computed(() => {
    // console.log('elevationOfVCard: ', (smAndUp.value) ? 2 : 0)
    return (smAndUp.value) ? 2 : 0
})

const email = ref("")
const password = ref("")
const confirmPassword = ref("")
const errorMessages = ref([])
const loading = ref(false)
const visiblePassword = ref(false)
const visibleConfirmPassword = ref(false)
const formSignUp = ref(false)

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
    sameValue: value => value === password.value || "Mật khẩu không trùng khớp."
})

const conditionsOfPassword = computed(() => {
    return {
        onlyAlphabetNumberSpecialChar: regex(/^[a-zA-Z0-9!@#$%^&*()-_=+{}\|:;"<>,.?\/]*$/g),
        atLeastOneUpperCaseLetterOneLowerCaseLetter: regex(/^(?=.*[a-z])(?=.*[A-Z]).+$/g),
        atLeastOneNumber: regex(/\d+/g),
        atLeastOneSpecialChar: regex(/^(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).*$/gm),
        length: () => {
            if (password.value.length < 8) {
                if (formSignUp.value)
                    formSignUp.value = false
                return false
            }
            return true
        }
    }
})

function regex(value) {
    const pattern = value
    if (!pattern.test(password.value)) {
        if (formSignUp.value) formSignUp.value = false
        return false
    }
    return true
}

async function autoLogin() {
    try {
        // loading.value = true
        const res = await AuthService.loginStep2({ email: email.value, password: password.value })
        if(res.status === 200) {
            // console.log(res.data)
            useUserStore().setInfoUser(res.data)
            setTimeout(() => router.push({name: "Home"}), 200)
        }
    } catch (error) {
        console.error("Auto-login: ",error)
    } finally {
        // loading.value = false
    }
}

async function submitFormSignUp() {
    try {
        if (!formSignUp.value)
            return
        loading.value = true
        let targetLanguage = navigator.language
        if(!targetLanguage.includes('zh') && targetLanguage.includes('-'))
            targetLanguage = targetLanguage.split('-')[0]
        const res = await AuthService.signup({ email: email.value, password: password.value , targetLanguage})
        // const existedEmail = await checkExistEmail()
        if(res.status === 201) {
            if(confirm("Bạn có muốn đăng nhập vào tài khoản này ngay không ?")) {
                await autoLogin()
            }
        }
    } catch (error) {
        const status = error.response.status
        if (status === 400)
            errorMessages.value.push("Email này đã được liên kết với một tài khoản khác !")
        else if (status === 401) 
            errorMessages.value.push("Email này có khả năng là giả. Vui lòng nhập địa chỉ email mà bạn đang sử dụng !")
        else
            console.warn(error)
    } finally {
        loading.value = false
    }
}

// Call API
async function checkExistEmail() {
    if (!email.value) return false

    // loading.value = true

    return email.value !== "nlnktpm@gmail.com"
}

function removeEmailNotExistError() {
    errorMessages.value = []
}

// onBeforeRouteLeave((to, from) => {
//     if (email.value.length + password.value.length + confirmPassword.value.length === 0)
//         return true
//     const answer = window.confirm(
//         'Dữ liệu đã nhập sẽ mất nếu bạn rời đi ?'
//     )
//     // cancel the navigation and stay on the same page
//     if (!answer) return false
// })

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