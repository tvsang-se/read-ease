<template>
    <div>
        <v-row no-gutters class="pa-2" id="statistics-wrapper">
            <v-col class="bg-white my-1 mx-2 pa-3 rounded elevation-5 statistics-container">
                <span class="font-weight-bold text-high-emphasis text-h6">Số tài khoản:</span>
                <span class="statistics font-weight-bold text-high-emphasis text-h5 float-right">{{
                    useUserStore().user.users.length }}</span>
            </v-col>
            <v-col class="bg-white my-1 mx-2 pa-3 rounded elevation-5 statistics-container">
                <span class="font-weight-bold text-high-emphasis text-h6">Lưu trữ: </span>
                <span class="statistics font-weight-bold text-high-emphasis text-h5 float-right">{{
                    Math.round(parseFloat(useUserStore().user.totalCapacity)) }} MB</span>
            </v-col>
            <v-col class="bg-white my-1 mx-2 pa-3 rounded elevation-5 statistics-container">
                <span class="font-weight-bold text-high-emphasis text-h6">Số tài liệu:</span>
                <span class="statistics font-weight-bold text-high-emphasis text-h5 float-right">{{
                    useUserStore().user.totalDocuments }}</span>
            </v-col>
            <v-col class="bg-white my-1 mx-2 pa-3 rounded elevation-5 statistics-container">
                <span class="font-weight-bold text-high-emphasis text-h6">Số chú thích:</span>
                <span class="statistics font-weight-bold text-high-emphasis text-h5 float-right">{{
                    useUserStore().user.totalAnnotations }}</span>
            </v-col>
        </v-row>
        <v-row no-gutters class="mt-1 mr-0 ">

            <v-col class="w-100 pl-4 pr-2" cols="6">
                <v-row no-gutters>
                    <v-col class="pa-0 ma-0">
                        <div class="pa-2 rounded bg-white elevation-5">
                            <user-created-bar-chart></user-created-bar-chart>
                        </div>
                    </v-col>
                </v-row>
                <v-row no-gutters class="mt-4">
                    <v-col>
                        <div class="px-2 py-3 rounded bg-white elevation-5">
                            <target-language-bar-chart :key="keyTargetLanguage"></target-language-bar-chart>
                        </div>
                    </v-col>
                </v-row>
            </v-col>
            <v-col class="w-100 px-4 pl-2" cols="6">
                <user-list :key="keyUserList"></user-list>
            </v-col>
        </v-row>
        <!-- <v-overlay persistent v-model="loading" class="mr-10 align-center justify-center">
            <v-progress-circular color="#00ADEF" indeterminate size="64"></v-progress-circular>
        </v-overlay> -->
    </div>
</template>

<script setup>
import UserList from '@/components/UserList.vue';
import UserCreatedBarChart from '@/components/UserCreatedBarChart.vue';
import TargetLanguageBarChart from '@/components/TargetLanguageBarChart.vue';
import { useUserStore } from '../store/user'
import { useAlertStore } from '../store/alert'
import Admin2Service from '../services/admin2.service';
import { storeToRefs } from 'pinia'
import { ref } from 'vue';

const userStore = useUserStore()
const { user } = storeToRefs(userStore)
const keyUserList = ref(0)
const keyTargetLanguage = ref(1)

async function getDataHome() {
    try {
        // useAlertStore().setAlert('warning','','Đang cập nhật dữ liệu mới nhất...', 9999)
        let res = await Admin2Service.getDataHome()
        // console.log(res.data)
        // res.data.listYear = res.data.listYear.map(year => year[0])
        user.value = { ...user.value, ...res.data }
        keyUserList.value += 1
        keyTargetLanguage.value +=1
        // setTimeout(() => useAlertStore().resetAlert(), 10000)
    } catch (error) {
        useAlertStore().showCommonError("")
        console.log(error)
    }
}

if (useUserStore().user?.token) {
    getDataHome()
}

</script>

<style scoped>
.statistics {
    color: #2196f3 !important;
    font-weight: 900;
}

.svg {
    display: block;
    width: 100%;
    height: 100%;
}

.statistics-container {
    cursor: pointer;
}
</style>