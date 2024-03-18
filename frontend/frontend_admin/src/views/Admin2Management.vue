<template>
    <v-data-table v-model:items-per-page="itemsPerPage" :search="search" :headers="headers" sticky hover
        :items-length="totalItems" :items="serverItems" :loading="loading" fixed-header loading-text="Đang tải thông tin..."
        no-data-text="Không tìm thấy tài khoản ADMIN 2 nào." items-per-page-text="Số dòng:"
        :items-per-page-options="itemsPerPageOptions" class="elevation-0" item-value="email" :sort-by="[{ key: 'lastAccess', order: 'desc' }]">
        <template v-slot:item.actions="{ item }">
            <v-icon class="me-2" @click="resetPasswordAdmin2(item)">mdi-lock-reset</v-icon>
            <v-icon class="me-2" @click="deleteAdmin2(item)">mdi-trash-can-outline</v-icon>
        </template>
        <template v-slot:item.lastAccess="{ value }">
            {{ formatTime(value) }}
        </template>
        <template v-slot:item.createAt="{ value }">
            {{ formatTime(value) }}
        </template>
        <template v-slot:top>
            <v-row no-gutters class="pa-1" style="background-color: rgb(0, 173, 239, 0.25);">
                <v-col cols="3" class="pr-6" @click="">
                    <div class="name-top d-flex justify-center align-center h-100">
                        <v-icon color="#00ADEF" size="small" class="mr-3">mdi-account-group-outline</v-icon>
                        Quản lý tài khoản ADMIN 2
                    </div>
                </v-col>
                <v-col cols="6"> <!-- class="d-flex align-center justify-space-between w-100" -->
                    <v-text-field density="compact" class="search-input" v-model.trim="search" variant="solo"
                        label="Nhập thông tin tài khoản ADMIN 2 cần tìm..." prepend-inner-icon="mdi-magnify" single-line
                        hide-details clearable></v-text-field>

                </v-col>
                <!-- <v-spacer></v-spacer> -->
                <v-col cols="" class="" align-self="center">
                    <div class="px-2 py-1 rounded elevation-1 ml-6 mr-2 float-right"
                        style="background-color: #00ADEF; width: fit-content; cursor: pointer;"
                        @click="createNewAdmin2Dialog = true">
                        <v-icon size="large" color="white">mdi-account-plus-outline</v-icon>
                        <span class="btn-title">Tạo tài khoản ADMIN 2</span>
                    </div>
                    <v-dialog v-model="createNewAdmin2Dialog" max-width="600px" persistent>
                        <v-card>
                            <v-card-title style="background-color: #00ADEF;">
                                <span class="text-h5 text-white">
                                    Thêm tài khoản ADMIN 2
                                </span>
                            </v-card-title>

                            <v-card-text class="pa-4 pb-0">
                                <v-form v-model="createNewAdmin2Form" @submit.prevent="createNewAdmin2">
                                    <v-text-field density="compact" v-model.trim="email" :readonly="loadingDialog"
                                        :rules="[rules.required, rules.email]" class="mb-2" clearable variant="outlined"
                                        :error-messages="errorMessages" label="Email" placeholder="Nhập email của tài khoản ADMIN 2 cần tạo mới..."
                                        @input="errorMessages = []"></v-text-field>
                                </v-form>
                            </v-card-text>

                            <v-card-actions>
                                <v-spacer></v-spacer>
                                <v-btn variant="elevated" width="120" @click="exitCreateNewAdmin2Dialog"
                                    :disabled="loadingDialog">
                                    Huỷ
                                </v-btn>
                                <v-btn variant="elevated" width="120" @click="createNewAdmin2"
                                    :disabled="!createNewAdmin2Form" :loading="loadingDialog">
                                    Thêm
                                </v-btn>
                            </v-card-actions>
                        </v-card>
                    </v-dialog>
                </v-col>
            </v-row>
        </template>
    </v-data-table>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import Admin1Service from '../services/admin1.service';
import { useUserStore } from '../store/user'
import { useRouter, useRoute } from 'vue-router'
import { useAlertStore } from '../store/alert'
import { storeToRefs } from 'pinia'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const { user } = storeToRefs(userStore)

const headers = ref([]),
    serverItems = ref([]),
    itemsPerPage = ref(10),
    loading = ref(false),
    totalItems = ref(0),
    search = ref(''),

    createNewAdmin2Dialog = ref(false),
    errorMessages = ref([]),
    email = ref(""),
    loadingDialog = ref(false),
    createNewAdmin2Form = ref(false)

async function getAllAdmin2() {
    try {
        loading.value = true
        let res = await Admin1Service.getAllAdmin2()
        serverItems.value = res.data
        totalItems.value = serverItems.value.length
    } catch (error) {
        useAlertStore().showCommonError("")
        console.log(error)
    } finally {
        loading.value = false
    }
}

getAllAdmin2()

headers.value = [
    { title: 'Email', key: 'email' },
    { title: 'Lần truy cập gần nhất', key: 'lastAccess' },
    { title: 'Ngày tạo tài khoản', key: 'createAt' },
    { title: '', key: 'actions' },
]

const itemsPerPageOptions = computed(() => {
    return [
        { title: '5', value: 5 },
        { title: '10', value: 10 },
        { title: '15', value: 15 },
        { title: '20', value: 20 },
        { title: '25', value: 25 },
        { title: 'Tất cả', value: totalItems.value },
    ]
})

async function createNewAdmin2() {
    if (!createNewAdmin2Form.value || email.value.length === 0)
        return
    try {
        loadingDialog.value = true
        const res = await Admin1Service.createNewAdmin2({ email: email.value })
        await getAllAdmin2()
        exitCreateNewAdmin2Dialog()
        useAlertStore().setAlert('success','','Tạo thành công tài khoản ADMIN 2 !', 3000)
    } catch (error) {
        const status = error.response.status
        if (status === 400)
            errorMessages.value.push("Email này đã được liên kết với tài khoản khác !")
        else if (status === 401)
            errorMessages.value.push("Email này có khả năng là giả. Vui lòng nhập địa chỉ email mà bạn đang sử dụng !")
        else {
            useAlertStore().showCommonError("")
            console.log(error)
        }
    } finally {
        loadingDialog.value = false
    }
}

async function deleteAdmin2(item) {
    if (!confirm(`Bạn chắc chắn muốn xoá tài khoản ADMIN 2 có email: ${item.email} ?`))
        return
    try {
        const res = await Admin1Service.deleteAdmin2(item.ID)
        useAlertStore().setAlert('success', '', 'Xoá thành công tài khoản ADMIN 2 !', 5000)
        await getAllAdmin2()
    } catch (error) {
        const status = error.response.status
        if (status === 400) {
            useAlertStore().setAlert('warning', '', 'Xoá thất bại do tài khoản ADMIN 2 hiện đang đăng nhập!', 5000)
        }
        else {
            useAlertStore().showCommonError("")
            console.log(error)
        }
    }
}

async function resetPasswordAdmin2(item) {
    if (!confirm(`Bạn muốn thiết lập lại mật khẩu cho tài khoản ADMIN 2 có email: ${item.email} ?`))
        return
    try {
        const res = await Admin1Service.resetPassword(item.ID)
        useAlertStore().setAlert('success', '', 'Đã gửi mật khẩu mới vào email của tài khoản ADMIN 2 !', 5000)
    } catch (error) {
        const status = error.response.status
        if (status === 404) {
            useAlertStore().setAlert('error', '', 'Thiết lập lại mật khẩu thất bại do không tìm thấy tài khoản ADMIN 2 tương ứng!', 5000)
        }
        else if (status === 400) {
            useAlertStore().setAlert('error', '', 'Thiết lập lại mật khẩu thất bại do tài khoản liên kết với email không có quyền ADMIN 2', 5000)
        }
        else {
            useAlertStore().showCommonError("")
            console.log(error)
        }
    }
}

function exitCreateNewAdmin2Dialog() {
    createNewAdmin2Dialog.value = false
    email.value = ""
}

const rules = ref({
    required: value => !!value || 'Trường này là bắt buộc.',
    email: value => {
        const pattern =
            /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
        return pattern.test(value) || 'Email không hợp lệ.'
    },
})

function formatTime(time) {
    const date = new Date(time)
    const hours = date.getHours();
    const minutes = date.getMinutes();
    const day = date.getDate();
    const month = date.getMonth() + 1; // Tháng trong JavaScript được đếm từ 0
    const year = date.getFullYear();

    // Định dạng thành chuỗi thời gian
    const formattedTime = (hours < 10 ? '0' : '') + hours + ':' +
        (minutes < 10 ? '0' : '') + minutes + ' ' +
        (day < 10 ? '0' : '') + day + '-' +
        (month < 10 ? '0' : '') + month + '-' +
        year;
    
    return formattedTime
}

</script>

<style >
.name-top {
    /* padding: 0px 6px !important; */
    font-weight: 800;
    font-size: 19px;
    color: #524f4f;
    /* color: rgb(238, 228, 228); */
    overflow-x: auto;
    white-space: nowrap;
    /* padding-top: 5px; */
    margin-left: 12px;
    cursor: pointer;
}

.btn-title {
    font-weight: 480;
    font-size: 18px;
    padding-left: 6px;
    padding-right: 6px;
    color: white;
}
</style>