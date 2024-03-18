<template>
    <v-row no-gutters class="text-white pa-2 pl-4 elevation-3" style="background-color: #00ADEF;">
        <v-col cols="12">
            <span style="font-size: 20px;">Thiết lập tài khoản</span>
        </v-col>
    </v-row>
    <v-row no-gutters class="ma-4" v-if="user?.token">
        <v-col class="">
            <v-card title="Thông tin cơ bản" elevation="2" rounded class="pa-2 mb-4">
                <v-form class="mx-3">
                    <v-text-field density="compact" v-model.trim="useUserStore().user.email" type="text" label="Email"
                        variant="outlined" readonly>
                    </v-text-field>
                    </v-form>
            </v-card>

            <v-card title="Đổi mật khẩu" elevation="2" rounded class="pa-2">
                <v-form @submit.prevent="submitFormChangePassword" v-model="formChangePassword" class="mx-3"
                    ref="ffromChangePassword">
                    <v-text-field density="compact" v-model.trim="currentPassword" maxlength="20" pattern="/^\P{Mn}*$/g"
                        :error-messages="errorMessages"
                        :append-inner-icon="visibleCurrentPassword ? 'mdi-eye-off' : 'mdi-eye'" class="mb-3"
                        :type="visibleCurrentPassword ? 'text' : 'password'" label="Mật khẩu hiện tại"
                        placeholder="Nhập vào mật khẩu hiên tại" variant="outlined"
                        @click:append-inner="visibleCurrentPassword = !visibleCurrentPassword" id="curent-password-field"
                        :rules="[rules.required, rules.password]" :readonly="loadingFormChangePassword"
                        @input="errorMessages = []">
                    </v-text-field>
                    <v-menu location="top" open-on-focus>
                        <template v-slot:activator="{ props }">
                            <v-text-field density="compact" v-model.trim="newPassword" maxlength="20" counter v-bind="props"
                                pattern="/^\P{Mn}*$/g" :append-inner-icon="visibleNewPassword ? 'mdi-eye-off' : 'mdi-eye'"
                                class="mb-3" :type="visibleNewPassword ? 'text' : 'password'" label="Mật khẩu mới"
                                placeholder="Nhập vào mật khẩu mới" variant="outlined"
                                @click:append-inner="visibleNewPassword = !visibleNewPassword" id="new-password-field"
                                :rules="[rules.required, rules.password]" :readonly="loadingFormChangePassword">
                            </v-text-field>
                        </template>

                        <v-list class="password__details pa-3">
                            <span>Mật khẩu phải thoả mãn các tiêu chí sau:</span> <br>
                            <div class="pl-sm-1">
                                <span>
                                    <v-icon v-if="newPassword.length === 0" color="warning" icon="mdi-alert-circle"
                                        size="x-small"></v-icon>
                                    <v-icon v-else-if="conditionsOfPassword.length()" color="success"
                                        icon="mdi-check-circle" size="x-small"></v-icon>
                                    <v-icon v-else color="error" icon="mdi-close-circle" size="x-small"></v-icon>
                                    Có độ dài từ 8 đến 20 ký tự.
                                </span> <br>
                                <span>
                                    <v-icon v-if="newPassword.length === 0" color="warning" icon="mdi-alert-circle"
                                        size="x-small"></v-icon>
                                    <v-icon v-else-if="conditionsOfPassword.onlyAlphabetNumberSpecialChar" color="success"
                                        icon="mdi-check-circle" size="x-small"></v-icon>
                                    <v-icon v-else color="error" icon="mdi-close-circle" size="x-small"></v-icon>
                                    Chỉ bao gồm chữ cái, chữ số, ký tự đặc biệt.
                                </span> <br>
                                <span>
                                    <v-icon v-if="newPassword.length === 0" color="warning" icon="mdi-alert-circle"
                                        size="x-small"></v-icon>
                                    <v-icon v-else-if="conditionsOfPassword.atLeastOneUpperCaseLetterOneLowerCaseLetter"
                                        color="success" icon="mdi-check-circle" size="x-small"></v-icon>
                                    <v-icon v-else color="error" icon="mdi-close-circle" size="x-small"></v-icon>
                                    Ít nhất một chữ cái viết hoa, viết thường.
                                </span> <br>
                                <span>
                                    <v-icon v-if="newPassword.length === 0" color="warning" icon="mdi-alert-circle"
                                        size="x-small"></v-icon>
                                    <v-icon v-else-if="conditionsOfPassword.atLeastOneNumber" color="success"
                                        icon="mdi-check-circle" size="x-small"></v-icon>
                                    <v-icon v-else color="error" icon="mdi-close-circle" size="x-small"></v-icon>
                                    Ít nhất một chữ số.
                                </span> <br>
                                <span>
                                    <v-icon v-if="newPassword.length === 0" color="warning" icon="mdi-alert-circle"
                                        size="x-small"></v-icon>
                                    <v-icon v-else-if="conditionsOfPassword.atLeastOneSpecialChar" color="success"
                                        icon="mdi-check-circle" size="x-small"></v-icon>
                                    <v-icon v-else color="error" icon="mdi-close-circle" size="x-small"></v-icon>
                                    Ít nhất một ký tự đặc biệt.
                                </span>
                            </div>
                        </v-list>
                    </v-menu>

                    <v-text-field density="compact" v-model.trim="confirmNewPassword" maxlength="20"
                        :append-inner-icon="visibleConfirmNewPassword ? 'mdi-eye-off' : 'mdi-eye'" class="mb-0"
                        :type="visibleConfirmNewPassword ? 'text' : 'password'" label="Xác nhận mật khẩu mới"
                        placeholder="Nhập lại mật khẩu mới" variant="outlined" :readonly="loadingFormChangePassword"
                        @click:append-inner="visibleConfirmNewPassword = !visibleConfirmNewPassword"
                        :rules="[rules.sameValue]">
                    </v-text-field>
                </v-form>
                <v-card-actions class="float-right">
                    <v-btn @click="resetFromChangePassword" :disabled="loadingFormChangePassword"
                        variant="elevated">Huỷ</v-btn>
                    <v-btn @click="submitFormChangePassword" :disabled="!formChangePassword || newPassword !== confirmNewPassword"
                        :loading="loadingFormChangePassword" variant="elevated">Cập nhật</v-btn>
                </v-card-actions>
            </v-card>
        </v-col>
    </v-row>
</template>

<script setup>
import Admin2Service from '../services/admin2.service';
import { ref, computed, watch } from 'vue'
import { useUserStore } from '../store/user'
import { useAlertStore } from '../store/alert'
import { storeToRefs } from 'pinia'

const userStore = useUserStore()
const { user } = storeToRefs(userStore)

const newPassword = ref(""),
    confirmNewPassword = ref(""),
    currentPassword = ref(""),
    visibleCurrentPassword = ref(false),
    visibleNewPassword = ref(false),
    visibleConfirmNewPassword = ref(false),
    formChangePassword = ref(false),
    loadingFormChangePassword = ref(false),
    errorMessages = ref([]),
    ffromChangePassword = ref(null)

async function submitFormChangePassword() {
    if (!formChangePassword.value)
        return
    try {
        loadingFormChangePassword.value = true
        const res = await Admin2Service.changePassword({
            userID: useUserStore().user.userID,
            newPwd: newPassword.value,
            oldPwd: currentPassword.value
        })
        useAlertStore().setAlert('success', '', "Đổi mật khẩu thành công", 5000)
        ffromChangePassword.value.reset()
        // console.log(res)
    } catch (error) {
        if (error.response.status === 400) {
            errorMessages.value.push("Mật khẩu không đúng !")
        } else {
            useAlertStore().showCommonError("")
            console.log(error)
        }
    } finally {
        loadingFormChangePassword.value = false
    }
}

function resetFromChangePassword() {
    ffromChangePassword.value.reset()
}

const rules = ref({
    required: value => !!value || 'Trường này là bắt buộc.',
    counter: value => value.length >= 8 || 'Mật khẩu phải có ít nhất 8 ký tự.',
    password: value => {
        const pattern =
            /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?])[A-Za-z\d!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]{8,20}$/gm

        return pattern.test(value) || 'Mật khẩu không hợp lệ.'
    },
    sameValue: value => value === newPassword.value || "Mật khẩu không trùng khớp.",
})

const conditionsOfPassword = computed(() => {
    return {
        onlyAlphabetNumberSpecialChar: regex(/^[a-zA-Z0-9!@#$%^&*()-_=+{}\|:;"<>,.?\/]*$/g),
        atLeastOneUpperCaseLetterOneLowerCaseLetter: regex(/^(?=.*[a-z])(?=.*[A-Z]).+$/g),
        atLeastOneNumber: regex(/\d+/g),
        atLeastOneSpecialChar: regex(/^(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).*$/gm),
        length: () => {
            if (newPassword.value.length < 8) {
                if (formChangePassword.value)
                    formChangePassword.value = false
                return false
            }
            return true
        }
    }
})

function regex(value) {
    const pattern = value
    if (!pattern.test(newPassword.value)) {
        if (formChangePassword.value) formChangePassword.value = false
        return false
    }
    return true
}

</script>