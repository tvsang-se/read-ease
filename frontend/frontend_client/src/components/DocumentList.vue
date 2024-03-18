<template>
    <div>
        <div class="overflow-y-hidden document-list">
            <v-data-table-server v-model:items-per-page="itemsPerPage" :search="search" :headers="headers" sticky hover
                width="1000px" :items-length="totalItems" :items="serverItems" fixed-header
                loading-text="Đang tải tài liệu..." :no-data-text="noDataText" items-per-page-text="Số dòng:"
                :sort-by="defaultSortBy" :items-per-page-options="itemsPerPageOptions" class="elevation-0" item-value="name"
                @update:options="loadItems" @click:row="(a, b) => gotoFileReader('id', b.item.id)">
                <template v-slot:item.star="{ value }">
                    <v-row no-gutters>
                        <v-col class="d-flex align-center" style="z-index: 0; margin-top: -8px;" v-if="value > 0">
                            <v-rating :model-value="value" hover color="yellow-darken-1" half-increments readonly
                                :size="20"></v-rating>
                            <span class="pl-1 pt-1">({{ value }})</span>
                        </v-col>
                        <span v-else>Chưa đánh giá</span>
                    </v-row>
                </template>
                <!-- <template v-slot:item.name="{ value }">
                <span class="name-file" @click="gotoFileReader('id', value.id)">{{ value.name }}</span>
            </template> -->
                <template v-slot:item.index="{ value }">
                    <v-menu open-on-hover location="bottom" persistent open-on-click>
                        <template v-slot:activator="{ props: menu }">
                            <!-- <v-icon v-bind="menu" color="#00ADEF" size="xx-large" class="mb-3"
                            icon="mdi-dots-horizontal-circle-outline"></v-icon> -->
                            <v-icon size="large" color="#00ADEF" v-bind="menu" class="pa-3 ma-0" @click=""> mdi-dots-horizontal </v-icon>
                        </template>
                        <v-list density="compact" class="mr-4">
                            <v-list-item density="compact" @click="gotoFileReader('index', value)">
                                <v-list-item-title>
                                    <v-icon class="mr-2" size="small"
                                        color="#00ADEF">mdi-book-open-page-variant-outline</v-icon>
                                    Đọc ngay
                                </v-list-item-title>
                            </v-list-item>
                            <v-list-item density="compact"
                                @click="loadAllItems(value, serverItems[value].id, serverItems[value].name)">
                                <v-list-item-title>
                                    <v-icon class="mr-2" size="small"
                                        color="#00ADEF">mdi-file-arrow-left-right-outline</v-icon>
                                    Thêm vào / Gỡ khỏi bộ sưu tập
                                </v-list-item-title>
                            </v-list-item>
                            <!-- <v-list-item density="compact" @click="">
                            <v-list-item-title>
                                <v-icon class="mr-2" size="small" color="#00ADEF">mdi-minus-box-outline</v-icon>
                                Xoá khỏi Bộ sưu tập
                            </v-list-item-title>
                        </v-list-item> -->
                            <v-list-item density="compact" @click="openEditForm(value, 'rename')">
                                <v-list-item-title>
                                    <v-icon class="mr-2" size="small" color="#00ADEF">mdi-form-textbox</v-icon>
                                    Đổi tên
                                </v-list-item-title>
                            </v-list-item>
                            <v-list-item density="compact" @click="openEditForm(value, 'rating')">
                                <v-list-item-title>
                                    <v-icon class="mr-2" size="small" color="#00ADEF">mdi-star-box-multiple-outline</v-icon>
                                    Đánh giá
                                </v-list-item-title>
                            </v-list-item>
                            <v-list-item density="compact" @click="deleteFile(value)">
                                <v-list-item-title>
                                    <v-icon class="mr-2" size="small" color="#00ADEF">mdi-trash-can-outline</v-icon>
                                    Xoá
                                </v-list-item-title>
                            </v-list-item>
                        </v-list>
                    </v-menu>

                </template>
                <template v-slot:top>
                    <v-row no-gutters class="pa-1" style="background-color: rgb(0, 173, 239, 0.25);">
                        <v-col cols="3" class="pr-6">
                            <div class="name-top">
                                <span v-if="route.fullPath === '/'">
                                    <v-icon color="#00ADEF" size="small">mdi-library-outline</v-icon>
                                    Thư viện ({{ totalItems }})
                                </span>
                                <span v-else-if="route.fullPath === '/bo-suu-tap/0'">
                                    <v-icon color="#00ADEF" size="small">mdi-bookshelf</v-icon>
                                    Bộ sưu tập ({{ user.collections.length }})
                                </span>
                                <span v-else :title="nameOfCollection">
                                    {{ nameOfCollection }} ({{ totalItems }})
                                </span>
                            </div>
                        </v-col>
                        <v-col cols="6"> <!-- class="d-flex align-center justify-space-between w-100" -->
                            <v-text-field density="compact" class="search-input" v-model.trim="name" variant="solo"
                                label="Nhập tên tài liệu cần tìm..." prepend-inner-icon="mdi-magnify" single-line
                                hide-details @keypress.enter="searchByName" @click:prepend-inner="searchByName" clearable
                                @click:clear="searchByName"></v-text-field>

                        </v-col>
                        <!-- <v-spacer></v-spacer> -->
                        <v-col cols="" class="" align-self="center">
                            <div @click="openAddNewDialog" class="px-2 py-1 rounded elevation-1 ml-6 mr-2 float-right"
                                style="background-color: #00ADEF; width: fit-content; cursor: pointer;">
                                <v-icon size="large" color="white">mdi-file-document-plus-outline</v-icon>
                                <span class="btn-title">Thêm tài liệu</span>
                            </div>
                            <v-dialog v-model="addNewDocumentDialog" max-width="600px" persistent>
                                <!-- <template v-slot:activator="{ props }"> -->
                                    <!-- <v-btn dark class="pr-0 " size="large" v-bind="props" prepend-icon="mdi-file-document-plus-outline">
                                        Thêm tài liệu mới
                                    </v-btn> -->
                                <!-- </template> -->
                                <v-card>
                                    <v-card-title style="background-color: #00ADEF;">
                                        <span class="text-h5 text-white">Thêm tài liệu</span>
                                    </v-card-title>

                                    <v-card-text>
                                        <v-container>
                                            <v-row no-gutters>
                                                <v-col cols="12">
                                                    <v-form v-model="uploadFileForm" ref="formUploadFile"
                                                        @submit.prevent="addNewDocument">
                                                        <v-text-field label="Tên tài liệu" density="compact"
                                                            variant="outlined" :error-messages="errorMessages"
                                                            :readonly="loadingDialog" ref="fileNameForm"
                                                            v-model="nameOfAddedFile"
                                                            :disabled="!nameOfAddedFile && !addedFile"
                                                            :rules="[rules.required, rules.pdfFile]"
                                                            @input="removeEmailNotExistError" class="mb-2"></v-text-field>
                                                        <v-select density="compact" label="Chọn loại tài liệu"
                                                            @update:modelValue="resetUploadFileForm"
                                                            :items="acceptedTypeOfFile" :readonly="loadingDialog"
                                                            v-model="typeOfFile" variant="outlined"></v-select>
                                                        <v-file-input variant="outlined" density="compact"
                                                            v-model="addedFile" :accept="`.${typeOfFile}`" :show-size="sizeFileOfFieldInput"
                                                            :readonly="loadingDialog" :clearable="!loadingDialog"
                                                            :rules="[rules.typeOfFile, rules.checkAvailableStore, rules.sizeOfFile]"
                                                            validate-on="input lazy">
                                                            <template v-slot:label>Nhấn để chọn tài liệu (.{{ typeOfFile }})
                                                                cần thêm</template>
                                                        </v-file-input>
                                                    </v-form>
                                                </v-col>
                                                <v-col cols="12" class="mt-3" v-show="typeOfFile !== 'pdf'">
                                                    <div class="bg-yellow pa-2 px-3 text-justify">
                                                        <span>
                                                            <i><b><u>Lưu ý:</u></b></i> Đối với các tập tin có phần mở rộng
                                                            không phải pdf, kích thước tối đa được tải lên là không quá 4.9
                                                            MB. Quá trình chuyển đổi sang tập tin dạng pdf có thể mất tới 5
                                                            phút. Vui lòng chờ trong thời gian thực hiện chuyển đổi !
                                                        </span>
                                                    </div>
                                                </v-col>
                                            </v-row>
                                        </v-container>
                                    </v-card-text>

                                    <v-card-actions>
                                        <v-spacer></v-spacer>
                                        <v-btn variant="elevated" width="120" @click="exitAddNewDocumentDialog"
                                            :disabled="loadingDialog">
                                            Huỷ
                                        </v-btn>
                                        <v-btn variant="elevated" width="120" @click="addNewDocument"
                                            :disabled="!addedFile || !uploadFileForm" :loading="loadingDialog">
                                            Thêm
                                        </v-btn>
                                    </v-card-actions>
                                </v-card>
                            </v-dialog>
                        </v-col>
                    </v-row>
                </template>
            </v-data-table-server>
        </div>
        <v-dialog v-model="editedItemDialog" max-width="600px" persistent>
            <v-card>
                <v-card-title style="background-color: #00ADEF;">
                    <span class="text-h5 text-white">
                        {{ typeEdit === 'rename' ? "Đổi tên tài liệu" : "Đánh giá tài liệu" }}
                    </span>
                </v-card-title>

                <v-card-text class="pa-0">
                    <v-container>
                        <v-row>
                            <v-col cols="12" class="pb-0">
                                <v-form v-model="editedItemForm" @submit.prevent="updateItem">
                                    <v-text-field label="Tên tài liệu" variant="outlined" :error-messages="errorMessages1"
                                        :autofocus="typeEdit === 'rename'"
                                        :readonly="loadingEditDialog || typeEdit === 'rating'"
                                        v-model="editedItem.name" :rules="[rules.required, rules.pdfFile]"
                                        @input="errorMessages1 = []" class="mb-2" :hide-details="typeEdit === 'rating'"></v-text-field>
                                </v-form>
                                <div class="d-flex justify-start align-center" v-if="typeEdit === 'rating'">
                                    <span class="font-weight-bold">Đánh giá tài liệu: </span>
                                    <div class="d-flex justify-center align-center ml-6">
                                        <v-rating v-model="editedItem.star" hover half-increments density="comfortable"
                                            size="40"></v-rating>
                                        <span class="text-body-1 font-weight-bold">({{ (editedItem.star >=
                                            0) ? editedItem.star : "Chưa đánh giá" }})</span>
                                    </div>
                                </div>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card-text>

                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn variant="elevated" width="120" @click="editedItemDialog = false" :disabled="loadingEditDialog">
                        Huỷ
                    </v-btn>
                    <v-btn variant="elevated" width="120" @click="updateItem"
                        :disabled="(!hasChangedFileName && typeEdit === 'rename') || !editedItemForm || (!hasChangedRating && typeEdit === 'rating')"
                        :loading="loadingEditDialog">
                        Cập nhật
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

    </div>

    <v-dialog v-model="selectItemsDialog" fullscreen :scrim="false" transition="dialog-bottom-transition">
        <v-card>
            <v-toolbar color="#00ADEF">
                <!-- <v-btn icon dark @click="selectItemsDialog = false">
                        <v-icon>mdi-close</v-icon>
                    </v-btn> -->
                <v-toolbar-title>
                    Chọn bộ sưu tập có tài liệu
                    <span class="font-weight-bold" :title="docName">{{ docName }}</span>
                </v-toolbar-title>
                <!-- <v-spacer></v-spacer> -->
                <v-toolbar-items>
                    <v-btn variant="text" @click="selectItemsDialog = false"> Huỷ </v-btn>
                    <v-btn variant="text" @click="updateListCollecionsOfDocument"> Lưu </v-btn>
                </v-toolbar-items>
            </v-toolbar>
            <v-data-table density="compact" v-model:items-per-page="itemsPerPage1" :search="search1" :headers="headers1"
                sticky hover v-model="selectedItems" :items-length="totalItems1" :items="allItems"
                :loading="loadingSelectItemsDialog" fixed-header loading-text="Đang tải tài liệu..."
                no-data-text="Không tìm thấy tài liệu nào." return-object show-select items-per-page-text="Số dòng:"
                :items-per-page-options="itemsPerPageOptions1" class="elevation-0" item-value="name"
                @click:row="(a, b) => toggleSelectedItem(b?.item)" height="550px">
                <template v-slot:top>
                    <v-row no-gutters class="pa-1" style="background-color: rgb(0, 173, 239, 0.25);">
                        <v-col cols="12"> <!-- class="d-flex align-center justify-space-between w-100" -->
                            <v-text-field density="compact" class="search-input" v-model.trim="search1" variant="solo"
                                label="Nhập thông tin bộ sưu tập cần tìm..." prepend-inner-icon="mdi-magnify" single-line
                                hide-details clearable></v-text-field>
                        </v-col>
                    </v-row>
                </template>
            </v-data-table>
        </v-card>
    </v-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import FileService from '../services/file.service';
import DriveService from '../services/drive.service';
import UserService from '../services/user.service';
import { useUserStore } from '../store/user'
import { useRouter, useRoute } from 'vue-router'
import { useAlertStore } from '../store/alert'
import { storeToRefs } from 'pinia'
import CollectionService from '@/services/collection.service';
import * as pdfjsLib from 'pdfjs-dist/build/pdf'
import "pdfjs-dist/web/pdf_viewer.css";

pdfjsLib.GlobalWorkerOptions.workerSrc =
    "https://cdn.jsdelivr.net/npm/pdfjs-dist@3.11.174/build/pdf.worker.min.js";


const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const { user } = storeToRefs(userStore)

const collectionID = computed(() => {
    if (route.fullPath === '/bo-suu-tap/0')
        return "0"
    else if (route.fullPath.includes('/bo-suu-tap')) {
        return route.fullPath.replace(/\D/g, "").toString()
    }
    else return "0"
})

const books = ref([]),
    headers = ref([]),
    serverItems = ref([]),
    itemsPerPage = ref(10),
    loading = ref(false),
    totalItems = ref(0),
    search = ref(''),
    name = ref(''),
    addNewDocumentDialog = ref(false),
    nameOfAddedFile = ref(''),
    typeOfFile = ref('pdf'),
    acceptedTypeOfFile = ref([]),
    addedFile = ref(null),
    errorMessages = ref([]),
    defaultSortBy = ref([{ key: 'lastRead', order: 'desc' }]),
    fileNameForm = ref(null),
    loadingDialog = ref(false),
    uploadFileForm = ref(null),
    formUploadFile = ref(null),
    openFileMenu = ref([]),

    editedItemDialog = ref(false),
    editedItemForm = ref(false),
    editedItem = ref({}),
    typeEdit = ref(""),
    loadingEditDialog = ref(false),
    errorMessages1 = ref([]),

    selectItemsDialog = ref(false),
    loadingSelectItemsDialog = ref(false),
    allItems = ref([]),
    currentItems = ref([]),
    selectedItems = ref([]),
    itemsPerPage1 = ref(100),
    search1 = ref(''),
    totalItems1 = ref(0),
    headers1 = ref([]);
let docID = -1, docIndex = -1;
const docName = ref('')
const sizeFileOfFieldInput = 1024


let currentCollection = null

acceptedTypeOfFile.value = ['pdf', 'doc', 'docx', 'ppt', 'pptx', 'xls', 'xlsx']

headers1.value = [
    { title: 'Tên bộ sưu tập', key: 'name' },
    { title: 'Số tài liệu', key: 'quantity' },
]

const itemsPerPageOptions1 = computed(() => {
    return [
        { title: '5', value: 5 },
        { title: '10', value: 10 },
        { title: '12', value: 12 },
        { title: '15', value: 15 },
        { title: '20', value: 20 },
        { title: '25', value: 25 },
        { title: 'Tất cả', value: totalItems.value },
    ]
})

watch(totalItems, (val) => {
    let t = []
    for(let i = 0; i<val; i++) {
        t.push(false)
    }
    openFileMenu.value = t
})
// console.log(route.params.id,user.value.collections.filter(collection => collection.ID == route.params.id)[0])
async function loadAllItems(index, id, name, newDocInCollection = false) {
    try {
        if (id === 0)
            return
        loadingSelectItemsDialog.value = true
        docID = id
        docName.value = name
        docIndex = index
        selectItemsDialog.value = true
        const paramObj = {
            page: 1,
            size: 9999,
            sortBy: 'lastRead',
            sortOrder: 'desc',
            name: '',
            collectionID: 0
        }
        const { data: res } = await FileService.getCollections(docID)
        console.log(res)
        allItems.value = userStore.user.collections
        totalItems1.value = userStore.user.collections.length
        itemsPerPage1.value = totalItems1.value
        let t = []
        for (let i = 0, len = res.length; i < len; i++) {
            // console.log(res[i])
            t.push({
                name: res[i].name,
                ID: res[i].id,
                quantity: res[i].documents.length
            })
        }
        currentItems.value = t
        selectedItems.value = JSON.parse(JSON.stringify(t))
        if(newDocInCollection && currentCollection) {
            selectedItems.value.push(currentCollection) //user.value.collections.filter(collection => collection.ID == route.params.id)[0]
        }

        // if(colID !== 0)
        // selectedItems.value =
    } catch (error) {
        console.log(error)
        useAlertStore().showCommonError('')
    } finally {
        loadingSelectItemsDialog.value = false
    }
}



function toggleSelectedItem(item) {
    console.log(item)
    if (!item)
        return
    for (let i = 0, len = selectedItems.value.length; i < len; i++) {
        if (item.ID === selectedItems.value[i].ID) {
            selectedItems.value.splice(i, 1)
            return
        }
    }
    selectedItems.value.push(item)
}

async function updateListCollecionsOfDocument() {
    if (docID === -1)
        return
    let add = [], remove = [];

    const current = currentItems.value.map((item) => item.ID)
    const selected = selectedItems.value.map((item) => item.ID)

    const setCurrent = new Set(current)
    const setSelected = new Set(selected)

    add = selected.filter((item) => !setCurrent.has(item))
    remove = current.filter((item) => !setSelected.has(item))

    console.log("currentItems",current)
    console.log("selectedItems",selected)
    console.log("add", add)
    console.log("remove", remove)

    try {
        let res, res1
        if (add.length)
            res = await FileService.addIntoListCollection(docID, add)
        if (remove.length)
            res1 = await FileService.removeOutListCollection(docID, remove)
        // console.log(res?.data, res1?.data)

        // serverItems.value[colIndex].quantity = selectedItems.value.length
        // user.value.collections = selectedItems.value
        // let res2 = await UserService.getInfoUser()
        // console.log("Updated", res2.data)

        for (let i = 0, len = userStore.user.collections.length; i < len; i++) {
            if (add.includes(userStore.user.collections[i].ID)) {
                userStore.user.collections[i].quantity += 1
            }
            if (remove.includes(userStore.user.collections[i].ID)) {
                userStore.user.collections[i].quantity -= 1
            }
        }
        if (route.fullPath !== '/') {
            await loadItems({ page: 1, itemsPerPage: itemsPerPage.value, sortBy: [] })
        }
        selectItemsDialog.value = false
    } catch (error) {
        console.log(error)
        useAlertStore().showCommonError("")
    }
}

const rules = ref({
    required: value => {
        if ((value.length === 0 && addedFile.value && addedFile.value[0]?.name) || (value.length === 0 && editedItemDialog))
            return "Trường này là bắt buộc."
        else return true
    },
    pdfFile: value => {
        if (!addedFile.value && !editedItemDialog)
            return true
        // const pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{0,30}$/
        // if (!value.endsWith(`.${typeOfFile.value}`))
        //     return `Tên tài liệu phải có kết thúc là .${typeOfFile.value}`
        const pattern = /^(?!.*[\\/:*?"<>|]).*$/gm // /^[^\\/:*?"<>|]/gm
        if (!pattern.test(value))
            return 'Tên tài liệu không được chứa những ký tự sau:  / : * ? " < > |'

        return true
        // if()
        // return pattern.test(value) || 'Tên tài liệu phải kết thúc bằng ".pdf" và không chứa những ký tự sau: \ / : * ? " < > |'
        //'Phải có ít nhất 1 chữ cái viết hoa, 1 chữ thường, 1 chữ số và 1 ký tự đặc biệt.'
        // 'At least one uppercase letter, one lowercase letter, one number and one special character'
    },
    typeOfFile: value => {
        if (!!value && value.length && value[0].name.includes(`${typeOfFile.value}`)) {
            // console.log("typeOfFile");
            return true
        }
        else return `Vui lòng chỉ chọn tài liệu có phần mở rộng là .${typeOfFile.value} !`
    },
    sizeOfFile: value => {
        const maxSize = (typeOfFile.value !== 'pdf') ? 4.9 : 100  // 1048576 = 1024^2
        if (!!value && value.length && value[0].size / 1048576 <= maxSize) { // file.size <= 100MB
            return true
        }
        else return `Vui lòng chỉ chọn tài liệu có kích cỡ nhỏ hơn ${maxSize} MB !`
    },
    checkAvailableStore: value => {
        if(!!value && value.length && value[0].size / 1048576 + user.value.totalCapacity > 1024)  {
            return `Dung lượng lưu trữ còn lại không đủ. Vui lòng xoá những tài liệu không cần nếu muốn thêm tài liệu này !`
        } else return true
    }
})
// watch(route, () => {
//     console.log("router", route.fullPath)
//     if(route.fullPath === '/') {
//         loadItems({ page: 1, itemsPerPage: itemsPerPage.value, sortBy: [] })
//     }
// }, {deep: true})



watch(addedFile, () => {
    removeEmailNotExistError()
    fileNameForm.value.resetValidation()
    if (rules.value.typeOfFile(addedFile.value) === true && rules.value.sizeOfFile(addedFile.value) === true) { //addedFile.value && addedFile.value[0]?.name
        let a = addedFile.value[0].name.split('.')
        a.pop()
        nameOfAddedFile.value = a.join('.')
    }
    else {
        nameOfAddedFile.value = ""
    }
})

watch(editedItemDialog, () => {
    if (!editedItemDialog.value) {
        editedItem.value = {}
        typeEdit.value = ""
        loadingEditDialog.value = false,
            errorMessages1.value = []
    }
})

setTimeout(() => loadItems({ page: 1, itemsPerPage: itemsPerPage.value, sortBy: [] }), 600)

const nameOfCollection = computed(() => {
    if (route.fullPath.includes('/bo-suu-tap')) {
        for (let i = 0; i < userStore.user.collections.length; i++) {
            if (userStore.user.collections[i].ID.toString() === collectionID.value) {
                currentCollection = userStore.user.collections[i]
                return userStore.user.collections[i].name
            }
        }
    }
    else {
        currentCollection = null
        return ""
    }
})

function resetUploadFileForm() {
    addedFile.value = null
    formUploadFile.value.resetValidation()
}

function removeEmailNotExistError() {
    errorMessages.value = []
}



headers.value = [
    { title: '', key: 'index', sortable: false },
    { title: 'Tên tài liệu', key: 'name' },
    { title: 'Đã đọc', key: 'numberOfPagesReading', sortable: false },
    { title: 'Lần đọc gần nhất', key: 'lastRead' },
    { title: 'Ngày tải lên', key: 'createAt' },
    { title: 'Đánh giá', key: 'star' },
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

function searchByName() {
    if (search.value !== name.value) {
        if (name.value === null)
            name.value = ""
        search.value = name.value
    }
}

const noDataText = computed(() => {
    // alert(loading.value === true ? 'Đang tải tài liệu...' : 'Không tìm thấy tài liệu nào.')
    return loading.value === true ? 'Đang tải tài liệu...' : 'Không tìm thấy tài liệu nào.'
})

function openAddNewDialog() {
    if(user.value.totalCapacity > 1024) {
        alert(`Dung lượng lưu trữ đã đầy [ ${user.value.totalCapacity.toFixed(2)} MB ]. Vui lòng xoá những tài liệu không cần thiết nếu muốn thêm tài liệu mới !`)
    }
    else {
        addNewDocumentDialog.value = true
    }
}

async function loadItems({ page, itemsPerPage, sortBy }) {
    try {
        // console.log((await UserService.getInfoUser()).data)
        // console.log(sortBy)
        loading.value = true
        const paramObj = {
            page: page,
            size: itemsPerPage,
            sortBy: 'lastRead',
            sortOrder: 'desc',
            name: search.value,
            collectionID: collectionID.value
        }
        if (sortBy.length) {
            paramObj.sortBy = sortBy[0].key
            paramObj.sortOrder = sortBy[0].order
        }
        const searchParams = new URLSearchParams(paramObj)
        let res = await FileService.getFiles(searchParams)
        // console.log(res)
        if (res.status < 200 && res.status > 299) {
            useAlertStore().showCommonError("")
            return
        }
        res = res.data
        books.value = []
        serverItems.value = []
        let t = []
        // console.log(res)
        for (let i = 1, len = res.length; i < len; i++) {
            // console.log(res[i])
            t.push({
                createAt: res[i].createAt,
                lastRead: res[i].lastRead,
                star: res[i].star,
                name: res[i].name,
                numberOfPagesReading: `${res[i].numberOfPagesReading}/${res[i].totalPages} (${res[i].percentPagesRead}%)`,
                index: i - 1,
                id: res[i].ID,
                url: res[i].url
            })
        }
        serverItems.value = t
        totalItems.value = res[0].totalBooks

    } catch (error) {
        console.log(error)
    } finally {
        loading.value = false
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

async function checkDuplicateName() {
    try {
        if (nameOfAddedFile.value.length === 0)
            return
        const res = await FileService.checkDuplicateName(new URLSearchParams({
            name: nameOfAddedFile.value + '.pdf'
        }))
        if (res.status >= 200 && res.status <= 299) {
            return true
        } else {
            return false
        }
    } catch (error) {
        if (error.response.status === 400) {
            // useAlertStore().setAlert('error', '', "Tên tập tin đã tồn tại trước đó.", 5000)
            errorMessages.value.push("Tên tài liệu đã tồn tại trước đó !")
        }
        return false
    }
}

async function addNewOfficeDocument() {
    try {
        loadingDialog.value = true
        const res = await FileService.addNewOfficeDocument(typeOfFile.value, nameOfAddedFile.value + '.pdf', userStore.user.idDriveFolder, addedFile.value[0])
        console.log(res)
        if (res.status >= 200 && res.status <= 299) {
            // useAlertStore().setAlert('success', '', 'Thêm tài liệu mới thành công !', 5000)
            await loadItems({ page: 1, itemsPerPage: itemsPerPage.value, sortBy: [] })
            user.value.totalCapacity += addedFile.value[0].size / 1048576
            user.value.totalDocument += 1
            await loadAllItems(0, res.data.id, res.data.name, true)
            exitAddNewDocumentDialog()
        }
    } catch (error) {
        if(error?.response?.status === 400)
            errorMessages.value.push("Tên tài liệu đã tồn tại trước đó !")
        else {
            useAlertStore().showCommonError('')
            console.log(error)
        }
    } finally {
        loadingDialog.value = false
    }
}

async function addNewDocument() {
    if (!uploadFileForm.value)
        return
    if (typeOfFile.value !== 'pdf') {
        addNewOfficeDocument()
        return
    }
    try {
        loadingDialog.value = true
        // const
        const checkDuplicate = await checkDuplicateName()
        if (!checkDuplicate) {
            loadingDialog.value = false
            return
        }
        if (!!!user.value?.accessTokenGGDrive) {
            await getAccessTokenGoogle()
        }
        const file = new Blob([addedFile.value[0]], { type: addedFile.value[0].type })
        const metadata = {
            name: nameOfAddedFile.value + '.pdf', // Filename at Google Drive
            mimeType: addedFile.value[0].type, // mimeType at Google Drive
            parents: [user.value.idDriveFolder], // Folder ID at Google Drive
        };

        let reader = new FileReader()
        let numPages;
        reader.onload = function () {
            let typedarray = new Uint8Array(this.result);

            const loadingTask = pdfjsLib.getDocument(typedarray);
            loadingTask.promise.then(pdf => {
                // console.log(pdf.numPages)
                numPages = pdf.numPages
            });
        }
        reader.readAsArrayBuffer(addedFile.value[0])
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

        const resGG = await DriveService.uploadFileToGGDrive(form, tempt.join(''))
        console.log("resGG", resGG)
        if (resGG.status < 200 || resGG.status > 299) {
            useAlertStore().showCommonError("")
            exitAddNewDocumentDialog()
            return
        }
        const { id: fileID, name } = resGG.data
        const result = {
            url: `https://www.googleapis.com/drive/v3/files/${fileID}?alt=media`,
            name,
            thumbnailLink: "",
            totalPages: numPages,
            userID: user.value.userID,
            size: parseFloat(addedFile.value[0].size) / 1048576 // Convert: Byte -> Megabytes (1024^2)
        }

        try {
            const res = await FileService.addNewDocument(result)
            console.log("addNewDocument", res)
            if (res.status >= 200 && res.status <= 299) {
                // useAlertStore().setAlert('success', '', 'Thêm tài liệu mới thành công !', 5000)
                await loadItems({ page: 1, itemsPerPage: itemsPerPage.value, sortBy: [] })
                user.value.totalCapacity +=  addedFile.value[0].size / 1048576
                user.value.totalDocument += 1
                exitAddNewDocumentDialog()
                loadAllItems(0, res.data.id, res.data.name, true)
            }
        } catch (error) {
            // if (error.response.status === 400) {
            //     // useAlertStore().setAlert('error', '', "Tên tập tin đã tồn tại trước đó.", 5000)
            //     errorMessages.value.push("Tên tài liệu đã tồn tại trước đó")
            // }
            useAlertStore().showCommonError("")
            console.log(error)
        }
    } catch (error) {
        if (error?.response?.data.error.code === 401) {
            await getAccessTokenGoogle()
            addNewDocument()
        } else {
            console.log(error)
            useAlertStore().showCommonError("")
        }
    } finally {
        loadingDialog.value = false
    }
}

function exitAddNewDocumentDialog() {
    addNewDocumentDialog.value = false
    addedFile.value = null
    // nameOfAddedFile.value = null
}

let hasChangedFileName = computed(() => {
    return editedItem.value && editedItem.value?.name?.length > 0 && editedItem.value?.name.trim() + '.pdf' !== serverItems.value[editedItem.value?.index].name
})

let hasChangedRating = computed(() => {
    return editedItem.value && editedItem.value?.star !== serverItems.value[editedItem.value?.index]?.star
})

function openEditForm(index, type) {
    editedItem.value = { ...serverItems.value[index], index, name: serverItems.value[index].name }
    if(type === 'rename') {
        let a = serverItems.value[index].name.split('.')
        a.pop()
        editedItem.value.name = a.join('.')
    }
    typeEdit.value = type
    editedItemDialog.value = true
}

function updateItem() {
    if (!editedItemForm.value)
        return
    if (typeEdit.value === 'rename')
        renameFile()
    else if (typeEdit.value === 'rating')
        ratingFile()
}

async function renameFile() {
    try {
        if (!hasChangedFileName.value)
            return
        loadingEditDialog.value = true
        const res = await FileService.renameFile({
            ID: editedItem.value.id,
            name: editedItem.value.name + '.pdf'
        })
        serverItems.value[editedItem.value.index].name = editedItem.value.name + '.pdf'
        editedItemDialog.value = false

    } catch (error) {
        if (error.response.status === 400) {
            // useAlertStore().setAlert('error', '', "Tên tập tin đã tồn tại trước đó.", 5000)
            errorMessages1.value.push("Tên tài liệu đã tồn tại trước đó !")
        } else {
            useAlertStore().showCommonError("")
            console.log(error)
        }
    } finally {
        loadingEditDialog.value = false
    }

}

async function ratingFile() {
    try {
        editedItemDialog.value = true
        loadingEditDialog.value = true
        const res = await FileService.ratingFile({
            ID: editedItem.value.id,
            star: editedItem.value.star
        })
        serverItems.value[editedItem.value.index].star = editedItem.value.star
        // alert(`Rating: ${editedItem.value.star}`)
        editedItemDialog.value = false
    } catch (error) {
        useAlertStore().showCommonError("")
        console.log(error)
    } finally {
        loadingEditDialog.value = false
    }
}

function getIdFromUrlGGDrive(url) {
    var regex = /[-\w]{25,}/;
    var match = url.match(regex);
    return match ? match[0] : null;
}

async function deleteFile(index) {
    try {
        if (!confirm(`Bạn có chắc chắn muốn xoá tài liệu: ${serverItems.value[index].name} không ?`)) {
            return
        }
        const res = await FileService.deleteFile(serverItems.value[index].id)
        await deleteFileOnGGDrive(getIdFromUrlGGDrive(serverItems.value[index].url))
        // console.log(res)
        loadItems({ page: 1, itemsPerPage: itemsPerPage.value, sortBy: [] })
        user.value.totalDocument -= 1
        const { data: res1 } = await CollectionService.getCollections()
        let t = []
        for (let i = 0, len = res1.length; i < len; i++) {
            // console.log(res[i])
            t.push({
                name: res1[i].name,
                ID: res1[i].id,
                quantity: res1[i].documents.length
            })
        }
        user.value.collections = t
        user.value.totalCapacity = res.data.totalCapacity
        useAlertStore().setAlert("success", "", "Xoá tài liệu thành công", 5000)
    } catch (error) {
        useAlertStore().showCommonError("")
        console.log(error)
    }
}

async function deleteFileOnGGDrive(fileGGID) {
    try {
        if (!user.value?.accessTokenGGDrive) {
            await getAccessTokenGoogle()
        }

        let tempt = user.value.accessTokenGGDrive.split('')
        const t = tempt[6]
        tempt[6] = tempt[9]
        tempt[9] = t
        const res = await DriveService.deleteFileOnGGDrive(fileGGID, tempt.join(''))
        // console.log(res)

    } catch (error) {
        if (error?.response?.data.error.code === 401) {
            await getAccessTokenGoogle()
            deleteFileOnGGDrive(fileGGID)
        } else {
            console.log(error)
            useAlertStore().showCommonError("")
        }
    }
}

function gotoFileReader(type, i) {
    if (type === 'id')
        router.push(`/tai-lieu/${i}`)
    else if (type === 'index')
        router.push(`/tai-lieu/${serverItems.value[i].id}`)
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
    padding-top: 5px;
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

/* width */
div.name-top::-webkit-scrollbar {
    height: 4px;
}

/* Track */
div.name-top::-webkit-scrollbar-track {
    /* background: #f1f1f1; */
    background: white;
}

/* Handle */
div.name-top::-webkit-scrollbar-thumb {
    background: #888;
}

/* Handle on hover */
div.name-top::-webkit-scrollbar-thumb:hover {
    background: #555;
}

.search-input .v-field__input {
    padding: 0px 6px !important;
}

.search-input .v-field {
    padding: 0px 8px !important;
}

.name-file {
    cursor: pointer;
}

.name-file:hover {
    color: #00ADEF;
}

th .v-selection-control.v-selection-control--density-default.v-checkbox-btn {
    padding-left: 8px;
}

th.v-data-table__td.v-data-table-column--align-start.v-data-table__th.v-data-table__th:nth-child(1),
td.v-data-table__td.v-data-table-column--align-start:nth-child(1),
th.v-data-table__td.v-data-table-column--align-start.v-data-table__th.v-data-table__th:nth-child(3),
td.v-data-table__td.v-data-table-column--align-start:nth-child(3),
th.v-data-table__td.v-data-table-column--align-start.v-data-table__th.v-data-table__th:nth-child(5),
td.v-data-table__td.v-data-table-column--align-start:nth-child(5) {
    padding: 0;
}

/* th.v-data-table__td.v-data-table-column--align-start.v-data-table__th.v-data-table__th:nth-child(1) {
    color: white;
    min-width: 35.991px;
} */

td.v-data-table__td.v-data-table-column--align-start:nth-child(1) {
    padding-left: 16px;
    width: 20px;
}

td.v-data-table__td.v-data-table-column--align-start:nth-child(2) {
    width: 600px;
}

.document-list .v-data-table__td.v-data-table-column--align-start.v-data-table__th.v-data-table__th:nth-child(3),
.document-list td.v-data-table__td.v-data-table-column--align-start:nth-child(3) {
    width: 150px;
}

td.v-data-table__td.v-data-table-column--align-start:nth-child(4) {
    width: 180px;
}

td.v-data-table__td.v-data-table-column--align-start:nth-child(5) {
    width: 148px;
}

td.v-data-table__td.v-data-table-column--align-start:nth-last-child() {
    width: 250px;
}

.v-table>.v-table__wrapper>table {
    min-width: 1255px;
}

.v-toolbar__content {
    height: 48px !important;
}
</style>
