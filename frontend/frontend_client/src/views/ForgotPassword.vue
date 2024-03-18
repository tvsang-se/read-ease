<template >
    <v-sheet class="w-100">
        <v-row align-content="center" justify="center" class="mr-0">
            <v-col md="5" sm="8" xs="12" class="pa-0 pa-sm-4">
                <v-card class="pa-6 pa-md-8" :elevation="elevationOfVCard">
                    <v-progress-linear :active="loading" :indeterminate="loading" absolute bottom
                        color="#00ADEF"></v-progress-linear>

                    <v-img class="mx-auto mb-2 login-signup-logo" src="../assets/logo_vietnamese.svg" max-width="240px"
                        width="" min-width="200px" @click="router.push({ name: 'Home' })"></v-img>

                    <v-card-item class="d-flex justify-center align-center mb-4">
                        <v-card-title class="text-md-h4 text-sm-h5 text-center pb-2" id="text-title">
                            {{ (currentStep !== -1) ? "Quên mật khẩu" : "Liên kết đã hết hạn" }}
                        </v-card-title>
                    </v-card-item>

                    <div v-if="currentStep === 1">
                        <v-form @submit.prevent="forgotPasswordStep1" v-model="formEmail">
                            <v-text-field v-model.trim="email" :rules="[rules.required, rules.email]" class="mb-2" clearable
                                variant="outlined" :error-messages="errorMessages" label="Email"
                                placeholder="Nhập email của tài khoản đã quên mật khẩu"
                                @input="removeEmailNotExistError"></v-text-field>
                        </v-form>
                        <br>

                        <v-card-actions class="d-flex justify-space-between pa-0">
                            <v-btn size="small" variant="text" class="text-body-1" @click="router.push({ name: 'Login' })">
                                Đăng nhập
                            </v-btn>

                            <v-btn :disabled="!formEmail" size="large" variant="flat" @click="forgotPasswordStep1">
                                Tiếp tục
                            </v-btn>
                        </v-card-actions>
                    </div>

                    <div v-else-if="currentStep === 2" class="text-center" style="margin-top: -28px; margin-bottom: 16px;">
                        <!-- <v-icon icon="mdi-email-lock" class="w-100"></v-icon> -->
                        <v-img src="../assets/mail_lock_white.svg" max-width="80px" class="mx-auto"></v-img>
                        Đường dẫn đổi mật khẩu đã được gửi đến địa chỉ email <br>
                        <span style="color: #00ADEF;" class="font-weight-medium">{{ email }}</span> <br>
                        Vui lòng kiểm tra mục thư rác nếu không tìm thấy .
                    </div>

                    <div v-else-if="currentStep === 3">
                        <v-form @submit.prevent="forgotPasswordStep3" v-model="formResetPassword">
                            <v-menu location="top" open-on-focus>
                                <template v-slot:activator="{ props }">
                                    <v-text-field v-model.trim="password" maxlength="20" counter v-bind="props"
                                        pattern="/^\P{Mn}*$/g"
                                        :append-inner-icon="visiblePassword ? 'mdi-eye-off' : 'mdi-eye'" class="mb-3"
                                        :type="visiblePassword ? 'text' : 'password'" label="Mật khẩu mới"
                                        placeholder="Nhập vào mật khẩu mới" variant="outlined"
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
                                            <v-icon
                                                v-else-if="conditionsOfPassword.atLeastOneUpperCaseLetterOneLowerCaseLetter"
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
                                :type="visibleConfirmPassword ? 'text' : 'password'" label="Xác nhận mật khẩu mới"
                                placeholder="Nhập lại mật khẩu mới" variant="outlined"
                                @click:append-inner="visibleConfirmPassword = !visibleConfirmPassword"
                                :rules="[rules.sameValue]">
                            </v-text-field>
                        </v-form>

                        <v-card-actions class="pa-0">

                            <v-btn block :disabled="!formResetPassword || password !== confirmPassword" size="large" variant="flat"
                                @click="forgotPasswordStep3">
                                Thiết lập lại mật khẩu
                            </v-btn>
                        </v-card-actions>
                    </div>

                    <div v-else-if="currentStep === 4" class="text-center text-h6 text-md-h5">
                        Chúc mừng, <br>
                        Bạn đã thiết lập lại mật khẩu thành công !
                        <v-card-actions class="pa-0 mt-6">
                            <v-btn block size="large" variant="flat" @click="router.push({ name: 'Login' })">
                                Đăng nhập ngay
                            </v-btn>
                        </v-card-actions>
                    </div>

                    <div v-else-if="currentStep === -1" class="text-center text-wrap" style="margin-top: -16px;">
                        <div class="px-1">Để thiết lập lại mật khẩu, vui lòng trở lại trang "Đăng nhập", nhập email, chọn
                            vào "Quên mật khẩu ?" để được gửi một liên kết mới.</div>
                        <v-card-actions class="pa-0 mt-6">
                            <v-btn block size="large" variant="flat" @click="router.push({ name: 'Login' })">
                                Trở lại trang Đăng nhập
                            </v-btn>
                        </v-card-actions>
                    </div>

                    <v-overlay v-model="loading" contained persistent />
                </v-card>
            </v-col>
        </v-row>
    </v-sheet>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useDisplay } from 'vuetify'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { useAlertStore } from '../store/alert'
import { useRoute } from 'vue-router';
import AuthService from '../services/auth.service';

const authStore = useAuthStore()
const alertStore = useAlertStore()
const route = useRoute()
const router = useRouter()
const email = ref("")
const formEmail = ref(false)
const loading = ref(false)
const errorMessages = ref([])
const { smAndUp } = useDisplay()
const elevationOfVCard = computed(() => {
    // console.log('elevationOfVCard: ', (smAndUp.value) ? 2 : 0)
    return (smAndUp.value) ? 2 : 0
})

const formResetPassword = ref(false)
const password = ref("")
const confirmPassword = ref("")
const visiblePassword = ref(false)
const visibleConfirmPassword = ref(false)
console.log(route)
const currentStep = ref(1)
if (route.query?.token) {
    forgotPasswordStep2()
}
else
    currentStep.value = 1

if (currentStep.value === 1)
    email.value = authStore.email

// router.push({
//     name: "NotFound",
//     params: { pathMatch: route.path.substring(1).split('/') },
//     // preserve existing query and hash if any
//     query: route.query,
//     hash: route.hash,
// })

// console.log(route.query?.token)

const rules = ref({
    required: value => !!value || 'Trường này là bắt buộc.',
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
                if (formResetPassword.value)
                    formResetPassword.value = false
                return false
            }
            return true
        }
    }
})

function regex(value) {
    const pattern = value
    if (!pattern.test(password.value)) {
        if (formResetPassword.value) formResetPassword.value = false
        return false
    }
    return true
}

async function forgotPasswordStep1() {
    try {
        if (!formEmail.value) return
        loading.value = true
        const res = await AuthService.forgotPasswordStep1(email.value)
        // const existedEmail =
        // console.log(res)
        if (res.status === 200) {
            // const res = await AuthService.forgotPasswordStep1({ email: email.value })
            // await forgotPasswordStep1()
            currentStep.value = 2
        }
    } catch (error) {
        const status = error.response.status
        if (status === 403)
            errorMessages.value.push("Email này chưa được liên kết với tài khoản nào !")
        else {
            useAlertStore().showCommonError('')
            console.warn(error)
        }
    } finally {
        loading.value = false
    }
}

async function forgotPasswordStep2() {
    try {
        const res = await AuthService.forgotPasswordStep2(route.query?.token)
        if (res.status === 200) {
            currentStep.value = 3

        }
    } catch (error) {
        const status = error.response.status
        if(status === 400) {
            currentStep.value = -1
        } else {
            alertStore.setAlert("error", "", "Đã có lỗi xảy ra, vui lòng thử lại!", 10000)
            currentStep.value = 1
        }
        console.error(error.response, status)
    } finally {
        loading.value = false
    }
}

async function forgotPasswordStep3() {
    try {
        const res = await AuthService.forgotPasswordStep3(route.query?.token, password.value)
        if (res.status === 200) {
            currentStep.value = 4
        }
    } catch (error) {
        const status = error.response.status
        if(status === 400) {
            currentStep.value = -1
        } else {
            alertStore.setAlert("error", "", "Đã có lỗi xảy ra, vui lòng thử lại!", 10000)
            currentStep.value = 1
        }
        console.error(error.response, status)
    } finally {
        loading.value = false
    }
}

function removeEmailNotExistError() {
    errorMessages.value = []
}

async function submitFormResetPassword() {
    try {
        loading.value = true
        // const res = await AuthService.forgotPasswordStep3({ email: email.value, password: password.value })
        const res = { status: 200 }
        if (res.status === 200) {
            currentStep.value = 4

        }
    } catch (error) {
        const status = error.response.status
        alertStore.setAlert("error", "", "Đã có lỗi xảy ra, vui lòng thử lại!", 10000)
        currentStep.value = 3
        console.error(error.response, status)
    } finally {
        loading.value = false
    }
}

</script>

<style scoped lang="scss">
@media (min-width: 960px) {
    .text-md-h4 {
        font-size: 28px !important;
    }
}
</style>
