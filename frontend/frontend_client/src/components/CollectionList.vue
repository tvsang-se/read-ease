<template>
    <div v-if="showCollectionList">
        <v-data-table v-model:items-per-page="itemsPerPage" :search="search" :headers="headers" sticky hover width="1000px"
            :items-length="totalItems" :items="serverItems" :loading="loading" fixed-header
            loading-text="Đang tải bộ sưu tập..." no-data-text="Không tìm thấy bộ sưu tập nào."
            items-per-page-text="Số dòng:" :items-per-page-options="itemsPerPageOptions" class="elevation-0"
            item-value="name" @click:row="(a, b) => openCollection(b.item.index)">
            <template v-slot:item.index="{ value }">
                <v-menu open-on-hover location="bottom" persistent>
                    <template v-slot:activator="{ props: menu }">
                        <!-- <v-icon v-bind="menu" color="#00ADEF" size="xx-large" class="mb-3"
                        icon="mdi-dots-horizontal-circle-outline"></v-icon> -->
                        <v-icon size="large" color="#00ADEF" v-bind="menu" class="pa-3 ma-0" @click=""> mdi-dots-horizontal </v-icon>
                    </template>
                    <v-list density="compact" class="mr-4">
                        <v-list-item density="compact" @click="openCollection(value)">
                            <v-list-item-title>
                                <v-icon class="mr-2" size="small" color="#00ADEF">mdi-folder-open-outline</v-icon>
                                Mở bộ sưu tập
                            </v-list-item-title>
                        </v-list-item>
                        <v-list-item density="compact"
                            @click="loadAllItems(value, serverItems[value].ID, serverItems[value].name)">
                            <v-list-item-title>
                                <v-icon class="mr-2" size="small"
                                    color="#00ADEF">mdi-folder-arrow-left-right-outline</v-icon>
                                Thêm/ Gỡ tài liệu
                            </v-list-item-title>
                        </v-list-item>
                        <v-list-item density="compact" @click="openEditForm(value, 'rename')">
                            <v-list-item-title>
                                <v-icon class="mr-2" size="small" color="#00ADEF">mdi-form-textbox</v-icon>
                                Đổi tên
                            </v-list-item-title>
                        </v-list-item>
                        <v-list-item density="compact" @click="deleteCollection(value)">
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
                    <v-col cols="3" class="pr-6" @click="">
                        <div class="name-top">
                            <v-icon color="#00ADEF" size="small">mdi-bookshelf</v-icon>
                            Bộ sưu tập ({{ user.collections.length }})
                        </div>
                    </v-col>
                    <v-col cols="6"> <!-- class="d-flex align-center justify-space-between w-100" -->
                        <v-text-field density="compact" class="search-input" v-model.trim="search" variant="solo"
                            label="Nhập thông tin bộ sưu tập cần tìm..." prepend-inner-icon="mdi-magnify" single-line
                            hide-details clearable></v-text-field>

                    </v-col>
                    <!-- <v-spacer></v-spacer> -->
                    <v-col cols="" class="" align-self="center">
                        <div class="px-2 py-1 rounded elevation-1 ml-6 mr-2 float-right"
                            style="background-color: #00ADEF; width: fit-content; cursor: pointer;"
                            @click="openEditForm(-1, 'create')">
                            <v-icon size="large" color="white">mdi-folder-plus-outline</v-icon>
                            <span class="btn-title">Thêm bộ sưu tập</span>
                        </div>
                        <v-dialog v-model="editedItemDialog" max-width="600px" persistent>
                            <!-- <v-btn dark class="pr-0 " size="large" v-bind="props" prepend-icon="mdi-file-document-plus-outline">
                                    Thêm tài liệu mới
                                </v-btn> -->
                            <v-card>
                                <v-card-title style="background-color: #00ADEF;">
                                    <span class="text-h5 text-white">
                                        {{ type === 'create' ? 'Thêm bộ sưu tập' : 'Đổi tên bộ sưu tập' }}
                                    </span>
                                </v-card-title>

                                <v-card-text class="pa-0">
                                    <v-container>
                                        <v-row>
                                            <v-col cols="12" class="pb-0">
                                                <v-form v-model="editedItemForm" @submit.prevent="submitEdiedItemForm">
                                                    <v-text-field label="Tên bộ sưu tập" variant="outlined" density="compact"
                                                        :error-messages="errorMessages" :readonly="loadingDialog"
                                                        v-model="editedItem.name" autofocus
                                                        :rules="[rules.required, rules.nameOfCollection]"
                                                        @input="removeEmailNotExistError" class="mb-2"></v-text-field>
                                                </v-form>
                                            </v-col>
                                        </v-row>
                                    </v-container>
                                </v-card-text>

                                <v-card-actions>
                                    <v-spacer></v-spacer>
                                    <v-btn variant="elevated" width="120" @click="exitEditedItemDialog"
                                        :disabled="loadingDialog">
                                        Huỷ
                                    </v-btn>
                                    <v-btn variant="elevated" width="120" @click="submitEdiedItemForm"
                                        :disabled="!editedItemForm || (type === 'rename' && !hasChangedCollectionName)"
                                        :loading="loadingDialog">
                                        {{ type === 'create' ? 'Thêm' : 'Cập nhật' }}
                                    </v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-dialog>
                    </v-col>
                </v-row>
            </template>
        </v-data-table>

        <v-dialog v-model="selectItemsDialog" fullscreen :scrim="false" transition="dialog-bottom-transition">
            <v-card>
                <v-toolbar color="#00ADEF">
                    <!-- <v-btn icon dark @click="selectItemsDialog = false">
                        <v-icon>mdi-close</v-icon>
                    </v-btn> -->
                    <v-toolbar-title>Chọn các tài liệu có trong bộ sưu tập <span class="font-weight-bold" :title="colName">{{ colName
                    }}</span></v-toolbar-title>
                    <!-- <v-spacer></v-spacer> -->
                    <v-toolbar-items>
                        <v-btn variant="text" @click="selectItemsDialog = false"> Huỷ </v-btn>
                        <v-btn variant="text" @click="updateListDocumentsOfCollection"> Lưu </v-btn>
                    </v-toolbar-items>
                </v-toolbar>
                <v-data-table density="compact" v-model:items-per-page="itemsPerPage1" :search="search1" :headers="headers1"
                    sticky hover v-model="selectedItems" :items-length="totalItems1" :items="allItems"
                    :loading="loadingSelectItemsDialog" fixed-header loading-text="Đang tải tài liệu..."
                    no-data-text="Không tìm thấy tài liệu nào." return-object show-select items-per-page-text="Số dòng:"
                    :items-per-page-options="itemsPerPageOptions1" class="elevation-0" item-value="name"
                    @click:row="(a, b) => toggleSelectedItem(b?.item)" height="550px">
                    <template v-slot:item.star="{ value }">
                        <v-row no-gutters>
                            <v-col class="d-flex align-center" style="z-index: 0;" v-if="value > 0">
                                <v-rating :model-value="value" hover color="yellow-darken-1" half-increments readonly
                                    :size="20"></v-rating>
                                <span class="pl-1 pt-1">({{ value }})</span>
                            </v-col>
                            <span v-else>Chưa đánh giá</span>
                        </v-row>
                    </template>
                    <template v-slot:top>
                        <v-row no-gutters class="pa-1" style="background-color: rgb(0, 173, 239, 0.25);">
                            <v-col cols="12"> <!-- class="d-flex align-center justify-space-between w-100" -->
                                <v-text-field density="compact" class="search-input" v-model.trim="search1" variant="solo"
                                    label="Nhập thông tin tài liệu cần tìm..." prepend-inner-icon="mdi-magnify" single-line
                                    hide-details clearable></v-text-field>

                            </v-col>
                        </v-row>
                    </template>
                </v-data-table>
            </v-card>
        </v-dialog>
    </div>

    <document-list v-else></document-list>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import DocumentList from '@/components/DocumentList.vue';
import FileService from '../services/file.service';
import CollectionService from '../services/collection.service';
import DriveService from '../services/drive.service';
import { useUserStore } from '../store/user'
import { useRouter, useRoute } from 'vue-router'
import { useAlertStore } from '../store/alert'
import { storeToRefs } from 'pinia'
import * as pdfjsLib from 'pdfjs-dist/build/pdf'
import "pdfjs-dist/web/pdf_viewer.css";

pdfjsLib.GlobalWorkerOptions.workerSrc =
    "https://cdn.jsdelivr.net/npm/pdfjs-dist@3.11.174/build/pdf.worker.min.js";

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const { user } = storeToRefs(userStore)

const books = ref([]),
    headers = ref([]),
    serverItems = ref([]),
    itemsPerPage = ref(10),
    loading = ref(false),
    totalItems = ref(0),
    search = ref(''),
    name = ref(''),

    editedItemDialog = ref(false),
    errorMessages = ref([]),
    editedItem = ref({}),
    loadingDialog = ref(false),
    editedItemForm = ref(false),
    type = ref(""),

    selectItemsDialog = ref(false),
    loadingSelectItemsDialog = ref(false),
    allItems = ref([]),
    currentItems = ref([]),
    selectedItems = ref([]),
    itemsPerPage1 = ref(100),
    search1 = ref(''),
    totalItems1 = ref(0),
    headers1 = ref([]);
let colID = 0, colIndex = -1;
const colName = ref('')

headers1.value = [
    { title: 'Tên tài liệu', key: 'name' },
    { title: 'Đã đọc', key: 'numberOfPagesReading', sortable: false },
    { title: 'Lần đọc gần nhất', key: 'lastRead' },
    { title: 'Ngày tải lên', key: 'createAt' },
    { title: 'Đánh giá', key: 'star' },
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
const defaultItem = {
    name: "",
    quantity: 0
}

const showCollectionList = computed(() => {
    if(route.fullPath === '/bo-suu-tap/0' && getCollections())
        return true
    else return false
})

const hasChangedCollectionName = computed(() => {
    return (type.value === 'rename' && editedItem.value?.name && editedItem.value.name.length > 0 && editedItem.value.name.trim() !== serverItems.value[editedItem.value.index].name.trim())
})

async function addDocument() {
    try {
        const res = await CollectionService.addDocument(1, 602)
        console.log("addDocument", res)
    } catch (error) {
        console.log(error)
    }
}

// watch(selectItemsDialog, () => {
//     if(selectItemsDialog.value)
//         loadAllItems()
// })

async function loadAllItems(index, id, name) {
    try {
        if (id === 0)
            return
        loadingSelectItemsDialog.value = true
        colID = id
        colName.value = name
        colIndex = index
        selectItemsDialog.value = true
        const paramObj = {
            page: 1,
            size: 9999,
            sortBy: 'lastRead',
            sortOrder: 'desc',
            name: '',
            collectionID: 0
        }
        let searchParams = new URLSearchParams(paramObj)
        const { data: res } = await FileService.getFiles(searchParams)
        // console.log(res)
        totalItems1.value = res[0].totalBooks
        itemsPerPage1.value = totalItems1.value
        paramObj.collectionID = colID
        searchParams = new URLSearchParams(paramObj)
        const { data: res1 } = await FileService.getFiles(searchParams)
        let t = []
        for (let i = 1, len = res.length; i < len; i++) {
            // console.log(res[i])
            t.push({
                ...res[i],
                numberOfPagesReading: `${res[i].numberOfPagesReading}/${res[i].totalPages} (${res[i].percentPagesRead}%)`,
            })
        }
        allItems.value = t

        t = []
        for (let i = 1, len = res1.length; i < len; i++) {
            // console.log(res[i])
            t.push({
                ...res1[i],
                numberOfPagesReading: `${res1[i].numberOfPagesReading}/${res1[i].totalPages} (${res1[i].percentPagesRead}%)`,
            })
        }
        currentItems.value = t
        selectedItems.value = JSON.parse(JSON.stringify(t))
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



// editedItemDialog = ref(false),
// editedItemForm = ref(false),
// editedItem = ref({}),
// typeEdit = ref(""),
// loadingEditDialog = ref(false),
// errorMessages1 = ref([])
// alert("Hello")
const rules = ref({
    required: value => {
        if (value?.length === 0)
            return "Trường này là bắt buộc."
        else return true
    },
    nameOfCollection: value => {
        // const pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{0,30}$/\
        const pattern = /^[^\\\/:*?"<>|]*$/;
        return pattern.test(value) || 'Tên bộ sưu tập không chứa những ký tự sau: \ / : * ? " < > |'
    },
})
// watch(route, () => {
//     console.log("router", route.fullPath)
//     if(route.fullPath === '/') {
//         loadItems({ page: 1, itemsPerPage: itemsPerPage.value, sortBy: [] })
//     }
// }, {deep: true})

// loadItems({ page: 1, itemsPerPage: itemsPerPage.value, sortBy: [] })

// serverItems.value = JSON.parse(JSON.stringify(userStore.user.collections))

async function getCollections() {
    if (!userStore.user?.collections)
        return
    // const res = await CollectionService.getCollections()
    let t = [], len = userStore.user.collections.length //res.data.length //
    totalItems.value = userStore.user.collections.length //res.data.length
    // console.log("getCollections",res)
    for (let i = 0; i < len; i++) {
        t.push({
            ...userStore.user.collections[i],
            index: i
        })
        // t.push({
        //     ...res.data[i],
        //     index: i,
        //     quantity: res.data[i].documents.length
        // })
    }
    serverItems.value = t
    // console.log("getCollections", serverItems.value)
    return true
}

// getCollections()

watch(editedItemDialog, () => {
    if (!editedItemDialog.value) {
        editedItem.value = {}
        type.value = ""
        loadingDialog.value = false,
            errorMessages.value = []
    }
})

function removeEmailNotExistError() {
    errorMessages.value = []
}

function submitEdiedItemForm() {
    editedItem.value.name = editedItem.value.name.trim()
    if (!editedItemForm.value || editedItem.value.name.length === 0)
        return
    if (type.value === 'create')
        createNewCollection()
    else if (type.value === 'rename')
        renameCollection()
}

headers.value = [
    { title: '', key: 'index', sortable: false },
    { title: 'Tên bộ sưu tập', key: 'name' },
    { title: 'Số tài liệu', key: 'quantity' },
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

async function loadItems({ page, itemsPerPage, sortBy }) {
    try {
        // console.log(sortBy)
        // loading.value = true
        const paramObj = {
            page: page,
            size: itemsPerPage,
            sortBy: 'lastRead',
            sortOrder: 'desc',
            name: search.value,
            collectionID: 0
        }
        if (sortBy.length) {
            paramObj.sortBy = sortBy[0].key
            paramObj.sortOrder = sortBy[0].order
        }
        const searchParams = new URLSearchParams(paramObj)
        // let res = await FileService.getFiles(searchParams)
        // let res1 = await CollectionService.getCollections()
        console.log(res1)
        // console.log(res)
        if (res.status < 200 && res.status > 299) {
            useAlertStore().showCommonError("")
            return
        }
        res = res.data
        books.value = []
        serverItems.value = []
        // console.log(res)
        for (let i = 1, len = res.length; i < len; i++) {
            // console.log(res[i])
            serverItems.value[i - 1] = {
                createAt: res[i].createAt,
                lastRead: res[i].lastRead,
                star: res[i].star,
                name: { name: res[i].name, ID: res[i].ID },
                numberOfPagesReading: `${res[i].numberOfPagesReading}/${res[i].totalPages} (${res[i].percentPagesRead}%)`,
                index: i - 1,
                ID: res[i].ID,
                url: res[i].url
            }
        }
        totalItems.value = res[0].totalBooks

    } catch (error) {
        console.log(error)
    } finally {
        // loading.value = false
    }
}

async function updateListDocumentsOfCollection() {
    if (colID === 0)
        return
    let add = [], remove = [];

    const current = currentItems.value.map((item) => item.ID)
    const selected = selectedItems.value.map((item) => item.ID)

    const setCurrent = new Set(current)
    const setSelected = new Set(selected)

    add = selected.filter((item) => !setCurrent.has(item))
    remove = current.filter((item) => !setSelected.has(item))

    // console.log("currentItems",current)
    // console.log("selectedItems",selected)
    console.log("add", add)
    console.log("remove", remove)

    try {
        let res, res1
        if (add.length)
            res = CollectionService.addDocuments(colID, add)
        if (remove.length)
            res1 = await CollectionService.removeDocuments(colID, remove)
        // console.log(res?.data, res1?.data)

        serverItems.value[colIndex].quantity = selectedItems.value.length
        user.value.collections[colIndex].quantity = selectedItems.value.length

        selectItemsDialog.value = false
    } catch (error) {
        console.log(error)
        useAlertStore().showCommonError("")
    }
}

async function createNewCollection() {
    try {
        loadingDialog.value = true
        // const 

        let res = await CollectionService.createNewCollection({ userID: userStore.user.userID, collectionName: editedItem.value.name })
        console.log(res)
        const newCollection = { ID: res.data.id, name: res.data.name, quantity: 0 }
        loadAllItems(serverItems.value.length, res.data.id, res.data.name)
        serverItems.value.push({ ...newCollection, index: serverItems.value.length })
        user.value.collections.push(newCollection)
        exitEditedItemDialog()
    } catch (error) {
        if (error?.response?.status === 400) {
            // useAlertStore().setAlert('error','',"Tên bộ sưu tập đã tồn tại.", 10000)
            errorMessages.value.push("Tên bộ sưu tập đã tồn tại.")
        } else {
            console.log(error)
            useAlertStore().showCommonError("")

            exitEditedItemDialog()
        }
    } finally {
        loadingDialog.value = false
    }
}

function exitEditedItemDialog() {
    editedItemDialog.value = false
    editedItem.value = {}
    // addedFile.value = null
    // nameOfCreatedCollection.value = null
}

function openEditForm(index, type1) {
    if (type1 === 'rename')
        editedItem.value = { ...serverItems.value[index], index }
    else if (type1 === 'create')
        editedItem.value = { ...defaultItem }
    else return
    type.value = type1
    editedItemDialog.value = true
}

async function renameCollection() {
    try {
        if (!hasChangedCollectionName.value)
            return
        loadingDialog.value = true
        const res = await CollectionService.renameCollection(editedItem.value.ID, {
            userID: userStore.user.userID,
            collectionName: editedItem.value.name
        })
        console.log(res)
        serverItems.value[editedItem.value.index].name = editedItem.value.name
        user.value.collections[editedItem.value.index].name = editedItem.value.name
        editedItemDialog.value = false

    } catch (error) {
        if (error.response.status === 400) {
            // useAlertStore().setAlert('error', '', "Tên tập tin đã tồn tại trước đó.", 5000)
            errorMessages.value.push("Tên tài liệu đã tồn tại trước đó !")
        } else {
            useAlertStore().showCommonError("")
            console.log(error)
        }
    } finally {
        loadingDialog.value = false
    }

}

// async function updateListDocumentsOfCollection() {
//     try {
//         // loadingDialog.value = true
//         // const 

//         let res = await CollectionService.createNewCollection({ userID: userStore.user.userID, collectionName: editedItem.value.name })
//         console.log(res)
//         // const newCollection = { id: res.data.id, name: res.data.name, quantity: 0 }
//         // serverItems.value.push(newCollection)
//         // user.value.collections.push(newCollection)
//     } catch (error) {
//         if (error?.response?.status === 400) {
//             // useAlertStore().setAlert('error','',"Tên bộ sưu tập đã tồn tại.", 10000)
//             // errorMessages.value.push("Tên bộ sưu tập đã tồn tại.")
//         } else {
//             useAlertStore().showCommonError("")
//         }
//         console.log(error)
//     } finally {
//         // loadingDialog.value = false
//     }
// }

async function deleteCollection(index) {
    try {
        console.log("deleteCollection", index)
        if (!confirm(`Bạn có chắc chắn muốn xoá bộ sưu tập: ${serverItems.value[index].name} không ?`)) {
            return
        }
        const res = await CollectionService.deleteCollection(serverItems.value[index].ID)
        serverItems.value.splice(index, 1)
        user.value.collections.splice(index, 1)
        if (index < serverItems.value.length)
            for (let i = index; i < serverItems.value.length; i++) {
                serverItems.value[i].index = parseInt(serverItems.value[i].index) - 1
            }
        // console.log(serverItems.value)
        useAlertStore().setAlert("success", "", "Xoá bộ sưu tập thành công", 5000)
    } catch (error) {
        useAlertStore().showCommonError("")
        console.log(error)
    }
}

function openCollection(index) {
    console.log("openCollection", index, serverItems.value[index])
    router.push(`/bo-suu-tap/${serverItems.value[index].ID}`)
}

</script>

<style >
.v-toolbar__content {
    height: 48px !important;
}

.dialog-bottom-transition-enter-active,
.dialog-bottom-transition-leave-active {
    transition: transform 0.2s ease-in-out;
}
</style>