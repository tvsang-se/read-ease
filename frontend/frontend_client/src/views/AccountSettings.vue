<template>
    <v-row no-gutters class="text-white pa-2 pl-4" style="background-color: #00ADEF;">
        <v-col cols="12">
            <span style="font-size: 20px;">Thiết lập tài khoản</span>
        </v-col>
    </v-row>
    <v-row no-gutters class="ma-4" v-if="user?.token">
        <v-col cols="4" class="mr-2">
            <v-row no-gutters>
                <v-col cols="12">
                    <v-card elevation="2">
                        <div class="bg-blue-lighten-5 flex-column pt-4 pb-2 d-flex justify-center align-center">
                            <v-avatar color="grey" size="140" rounded="1" class="">
                                <img crossorigin="anonymous" style="object-fit: cover; max-width: 100%" :src="user.avatar"/>
                            </v-avatar>
                            <v-list-item>
                                <v-list-item-title :title="user?.email">
                                    <span class="font-weight-bold text-h6">{{ user.email }}</span>
                                </v-list-item-title>
                            </v-list-item>
                        </div>
                        <v-card-item class="bg-blue">
                            <div class="pa-1 text-white" style="">
                                <span class="">Tổng thời gian truy cập: </span>
                                <span class="float-right font-weight-bold">{{ (user?.totalAccessTime / 3600).toFixed(0) }}
                                    giờ</span>
                            </div>
                            <div class="pa-1 text-white" style="">
                                <span class="">Bộ nhớ đã lưu trữ: </span>
                                <span class="float-right font-weight-bold"> {{ (user?.totalCapacity).toFixed(0) }} / 1024
                                    MB</span>
                            </div>
                            <div class="pa-1 text-white" style="">
                                <span class="">Số tài liệu: </span>
                                <span class="float-right font-weight-bold">{{ user?.totalDocument }}</span>
                            </div>
                            <div class="pa-1 text-white" style="">
                                <span class="">Số bộ sưu tập: </span>
                                <span class="float-right font-weight-bold">{{ user?.collections.length }}</span>
                            </div>
                        </v-card-item>
                    </v-card>
                </v-col>
            </v-row>
            <v-row no-gutters class="mt-4">
                <v-col cols="12">
                    <v-card elevation="2" title="Liên hệ" class="pa-2">
                        <div class="px-4 pb-4">
                            Cảm ơn bạn đã sử dụng ReadEase, trong quá trình sử dụng nếu có bất kỳ thắc mắc hay vấn đề nào
                            cần giải đáp
                            bạn có thể liên hệ với chúng tôi qua email:
                            <br>
                            <div class="text-center font-weight-bold" style="font-size: 17.5px;"><a
                                    href="mailto:readeaseapp@gmail.com">readeaseapp@gmail.com</a></div>
                        </div>
                    </v-card>
                </v-col>
            </v-row>
        </v-col>

        <v-col class="ml-2">
            <v-card title="Thông tin cơ bản" elevation="2" rounded class="pa-2 mb-4">
                <v-form @submit.prevent="submitFormUpdateInfoUser" class="mx-3" ref="ffromUpdateInfoUser"
                    v-model="formUpdateAvatar">
                    <v-row no-gutters>
                        <v-col cols="6">
                            <v-text-field density="compact" v-model.trim="useUserStore().user.email" type="text"
                                label="Email" variant="outlined" readonly>
                            </v-text-field>
                        </v-col>
                        <v-col class="ml-5">
                            <v-select density="compact" label="Dịch sang ngôn ngữ" :items="languagesArr"
                                :readonly="loadingFormUpdateInfoUser" v-model="targetLanguage"
                                variant="outlined"></v-select>
                        </v-col>
                    </v-row>
                    <v-file-input density="compact" variant="outlined" v-model="addedFile" accept="image/*" show-size
                        :readonly="loadingFormUpdateInfoUser" :rules="[rules.typeOfFile, rules.sizeOfFile]"
                        label="Tải ảnh đại diện" prepend-icon="mdi-image-outline"></v-file-input>
                </v-form>
                <v-card-actions class="float-right">
                    <v-btn @click="resetFromUpdateInfoUser" :disabled="loadingFormUpdateInfoUser"
                        variant="elevated">Huỷ</v-btn>
                    <v-btn @click="submitFormUpdateInfoUser" :disabled="!hasChanged" :loading="loadingFormUpdateInfoUser"
                        variant="elevated">Cập nhật</v-btn>
                </v-card-actions>
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
                    <v-btn @click="submitFormChangePassword"
                        :disabled="!formChangePassword || newPassword !== confirmNewPassword"
                        :loading="loadingFormChangePassword" variant="elevated">Cập nhật</v-btn>
                </v-card-actions>
            </v-card>
        </v-col>
    </v-row>
</template>

<script setup>
import { languagesObj, languagesArr } from "../etc/languages";
import DriveService from '../services/drive.service';
import UserService from '../services/user.service';
import FileService from '../services/file.service';
import { ref, computed } from 'vue'
import { useUserStore } from '../store/user'
import { useAlertStore } from '../store/alert'
import { useTranlationStore } from '../store/translation'
import { storeToRefs } from 'pinia'

const userStore = useUserStore()
const { user } = storeToRefs(userStore)

async function getInfoAccount() {
    try {
        const res = await UserService.getInfoUser()
        user.value.totalAccessTime = res.data.totalAccessTime
        user.value.totalCapacity = res.data.totalCapacity
        console.log(res.data)
        return true
    } catch (error) {
        useAlertStore().showCommonError('')
        console.log(error)
    }
}
getInfoAccount()

const targetLanguage = ref(''),
    formUpdateAvatar = ref(false),
    loadingFormUpdateInfoUser = ref(false),
    ffromUpdateInfoUser = ref(null),
    addedFile = ref(null)

targetLanguage.value = Object.keys(languagesObj).find(
    (key) => languagesObj[key] === user.value.targetLanguage
);

const targetLanguageCode = computed(() => {
    // if (!hasChanged.value)
    //     hasChanged.value = true
    return languagesObj[targetLanguage.value]
})

const hasChanged = computed(() => {
    // alert("alo")
    // console.log(!addedFile.value, targetLanguageCode.value !== user.value.targetLanguage, (addedFile.value && addedFile.value[0] && addedFile.value[0]?.name) && formUpdateAvatar.value)
    if (!addedFile.value && targetLanguageCode.value !== user.value.targetLanguage)
        return true

    if ((addedFile.value && addedFile.value[0] && addedFile.value[0]?.name) && formUpdateAvatar.value)
        return true

    return false

    // if (formUpdateAvatar.value && (targetLanguageCode.value !== user.value.targetLanguage || (addedFile.value && addedFile.value[0] && addedFile.value[0]?.name))) {
    //     // console.log('File',addedFile.value[0].type)
    //     return true
    // }
    // else return false
})

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
        const res = await UserService.changePassword({
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

async function submitFormUpdateInfoUser() {
    if (!hasChanged.value)
        return
    try {
        loadingFormUpdateInfoUser.value = true
        let avatar = user.value.avatar

        if (addedFile.value && addedFile.value[0] && addedFile.value[0]?.name) {
            if (!user.value?.accessTokenGGDrive) {
                await getAccessTokenGoogle()
            }
            const file = new Blob([addedFile.value[0]], { type: addedFile.value[0].type })
            const metadata = {
                name: addedFile.value[0].name, // Filename at Google Drive
                mimeType: addedFile.value[0].type, // mimeType at Google Drive
                parents: [user.value.idDriveFolder], // Folder ID at Google Drive

            };

            let form = new FormData();
            form.append(
                "metadata",
                new Blob([JSON.stringify(metadata)], { type: "application/json" })
            );

            form.append("file", file);
            let tempt = user.value.accessTokenGGDrive.split('')
            const t = tempt[6]
            tempt[6] = tempt[9]
            tempt[9] = t
            console.log(file)
            const resGG = await DriveService.uploadFileToGGDrive(form, tempt.join(''))
            console.log("resGG", resGG)
            if (resGG.status < 200 || resGG.status > 299) {
                useAlertStore().showCommonError("")
                // exitAddNewDocumentDialog()
                return
            }
            const { id: fileID } = resGG.data
            const ress = await DriveService.setPermissionsOfFileInGGDrive(fileID, tempt.join(''))
            // console.log(ress)
            if (avatar.includes("https://drive.lienuc.com/uc?id="))
                await DriveService.deleteFileOnGGDrive(avatar.split("=")[1], tempt.join(''))
            avatar = "https://drive.lienuc.com/uc?id=" + fileID
        }

        try {
            let res1 = await UserService.updateInfoUser({
                email: user.value.email,
                avatar,
                targetLanguage: targetLanguageCode.value
            })
            user.value.avatar = avatar
            user.value.targetLanguage = targetLanguageCode.value
            const targetLang = Object.keys(languagesObj).find(
                (key) => languagesObj[key] === targetLanguageCode.value
            );
            useTranlationStore().updateTargetLanguage(targetLang)
            useAlertStore().setAlert('success', '', "Cập nhật thông tin thành công", 5000)
            resetFromUpdateInfoUser()
            // console.log(avatar)
            // console.log("UpdateInfoUser", res1)
        } catch (error) {
            useAlertStore().showCommonError("")
            console.log(error)
        }

    } catch (error) {
        if (error?.response?.data.error.code === 401) {
            await getAccessTokenGoogle()
            submitFormUpdateInfoUser()
        } else {
            console.log(error)
            useAlertStore().showCommonError("")
        }
    } finally {
        loadingFormUpdateInfoUser.value = false
    }
}

async function getAccessTokenGoogle() {
    try {
        let res = await FileService.getGoogleAccessToken()
        if (res.status >= 200 && res.status <= 299) {
            res = res.data
            user.value.accessTokenGGDrive = res.token
        }
    } catch (error) {
        console.log(error)
    }
}

function resetFromUpdateInfoUser() {
    addedFile.value = null
    targetLanguage.value = Object.keys(languagesObj).find(
        (key) => languagesObj[key] === user.value.targetLanguage
    );
    ffromUpdateInfoUser.value.resetValidation()
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
    typeOfFile: value => {
        if (!!value && value.length && value[0].type.includes('image')) {
            return true
        }
        else return 'Vui lòng chỉ chọn tập tin có định dạng hình ảnh (.png, .jpg, .gif) !'
    },
    sizeOfFile: value => {
        if (!!value && value.length && value[0].size / 2 <= 1048576) { // file.size <= 2MB
            return true
        }
        else return 'Vui lòng chỉ chọn tập tin có kích cỡ nhỏ hơn 2 MB !'
    }
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
