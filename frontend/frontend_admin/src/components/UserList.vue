<template>
    <v-card flat density="compact" title="Danh sách tài khoản người dùng" class="bg-blue" elevation="5">
        <template v-slot:text>
            <v-text-field density="compact" v-model="search" variant="solo" class="elevation-0 search"
                label="Nhập thông tin người dùng cần tìm..." prepend-inner-icon="mdi-magnify" single-line hide-details
                clearable></v-text-field>
        </template>

        <div class="user-list">
            <v-data-table density="compact" v-model:items-per-page="itemsPerPage" :search="search" :headers="headers" sticky
                hover :items-length="totalItems" :items="serverItems" fixed-header loading-text="Đang tải thông tin..."
                no-data-text="Không tìm người dùng nào." items-per-page-text="Số dòng:" :height="heightOfDataTable"
                :items-per-page-options="itemsPerPageOptions" class="elevation-2" item-value="email"
                :sort-by="[{ key: 'lastAccess', order: 'desc' }]">
                <template v-slot:item.lastAccess="{ value }">
                    <span>{{ formatTime(value) }}</span>
                </template>
                <template v-slot:item.createAt="{ value }">
                    <span>{{ formatTime(value) }}</span>
                </template>
                <template v-slot:top>
                    <v-row no-gutters style="background-color: rgb(0, 173, 239, 0.25);" class="pa-1">
                        <v-col cols="" class="annotation pl-2">
                            <!-- class="d-flex align-center justify-space-between w-100" -->

                            <v-row no-gutters>
                                <v-col>
                                    <b>(1):</b> Tổng dung lượng đã lưu trữ (MB),
                                </v-col>
                                <v-col>
                                    <b>(2):</b> Tổng thời gian truy cập (giờ)
                                </v-col>
                            </v-row>
                        </v-col>
                    </v-row>
                </template>
            </v-data-table>
        </div>
    </v-card>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useUserStore } from '../store/user'
import { useDisplay } from 'vuetify'
import { storeToRefs } from 'pinia'

const userStore = useUserStore()
const { height } = useDisplay()

const headers = ref([]),
    serverItems = ref([]),
    totalItems = ref(0),
    search = ref(''),
    itemsPerPage = ref(totalItems.value)

serverItems.value = userStore.user.users
totalItems.value = serverItems.value.length
itemsPerPage.value = totalItems.value

headers.value = [
    { title: 'Email', key: 'email' },
    { title: '(1) (MB)', key: 'totalCapacity', align: 'end' },
    { title: '(2) (giờ)', key: 'totalAccessTime', align: 'end' },
    { title: 'Số tài liệu', key: 'totalDocuments', align: 'end' },
    { title: 'Lần truy cập gần nhất', key: 'lastAccess', align: 'end' },
    { title: 'Ngày tạo tài khoản', key: 'createAt', align: 'end' },
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

const heightOfDataTable = computed(() => {
    const e = document.getElementById('statistics-wrapper')
    if ((height.value || height.value === 0) && e) {
        const heigh = height.value - e.clientHeight - 8 - 198
        return `${heigh}px`
    }
    else return '416px'
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
.user-list .title-table * {
    font-size: 16px !important;
    line-height: 16px !important;
}

.user-list .search .v-field {
    padding-left: 8px;
    padding-right: 8px;
}

.user-list .search .v-field__input {
    padding: 4px !important;
}

.user-list .v-table__wrapper table {
    min-width: 1015px !important;
}

.user-list th.v-data-table__td.v-data-table-column--align-end.v-data-table__th.v-data-table__th:nth-child(2),
.user-list td.v-data-table__td.v-data-table-column--align-end:nth-child(2),
.user-list th.v-data-table__td.v-data-table-column--align-end.v-data-table__th.v-data-table__th:nth-child(3),
.user-list td.v-data-table__td.v-data-table-column--align-end:nth-child(3),
.user-list th.v-data-table__td.v-data-table-column--align-end.v-data-table__th.v-data-table__th:nth-child(4),
.user-list td.v-data-table__td.v-data-table-column--align-end:nth-child(4),
.user-list th.v-data-table__td.v-data-table-column--align-end.v-data-table__th.v-data-table__th:nth-child(5),
.user-list td.v-data-table__td.v-data-table-column--align-end:nth-child(5) {
    padding-right: 8px !important;
    padding-left: 8px !important;
}

.user-list th.v-data-table__td.v-data-table-column--align-start.v-data-table__th.v-data-table__th:first-child,
.user-list td.v-data-table__td.v-data-table-column--align-start:first-child {
    padding-left: 16px !important;
    padding-right: 8px !important;
}

.user-list th.v-data-table__td.v-data-table-column--align-start.v-data-table__th.v-data-table__th:nth-child(6),
.user-list td.v-data-table__td.v-data-table-column--align-start:nth-child(6) {
    padding-right: 16px !important;
    padding-left: 8px !important;
}

/* 
.user-list td.v-data-table__td.v-data-table-column--align-end:nth-child(4) {
    min-width: 100px;
} */
</style>