<template>
    <v-card flat>
        <v-card-text class="pa-0">
            <v-expansion-panels variant="accordion" multiple v-show="annotationObjView" v-model="expansions">
                <v-expansion-panel v-for="(annotations, pageNumber) in annotationObjView" :key="pageNumber" elevation="0"
                    :value="pageNumber">
                    <v-expansion-panel-title>
                        <div class="w-100 d-flex justify-space-between mr-3">
                            <span class="font-weight-medium">{{ `Trang ${pageNumber}` }}</span>
                            <span class="font-weight-medium text-disabled ">{{ annotations.length }}</span>
                        </div>
                    </v-expansion-panel-title>
                    <v-expansion-panel-text style="background-color: #F5F5F5;">
                        <div class="annotation-container d-flex mb-1 mt-1 elevation-2" :id="annotation.id"
                            v-for="(annotation, idx) in annotations" :key="annotation.id"
                            @click="gotoAnnotation(annotation.id)">
                            <div v-if="annotation.target.selector?.strokeColor"
                                :style="{ width: '8px', backgroundColor: annotation.target.selector.strokeColor, opacity: annotation.target.selector?.opacity }">

                            </div>

                            <div class="flex-grow-1 pa-1 pr-1 pb-2"
                                :class="{ 'annotation-selected': annotation.id === annotationSelected.id }">
                                <div class="d-flex justify-space-between mb-1">
                                    <span
                                        :class="[(annotation.id === annotationSelected.id) ? 'text-high-emphasis font-weight-black' : 'text-disabled font-weight-medium']">
                                        {{ getHeaderAnnotation(annotation.bodyValue, annotation.target.selector.subtype) }}
                                    </span>
                                    <v-icon color="#00ADEF" size="default"
                                        @click="deleteAnnotation(annotation, idx)">mdi-trash-can-outline</v-icon>
                                </div>
                                <div>
                                    <v-textarea variant="outlined" @click="onFocus(annotation.id)"
                                        @update:focused="e => updateAnnotation(e, pageNumber, idx)"
                                        :class="{ 'set-min-height': annotation.bodyValue.length <= 31 }"
                                        v-model="annotation.bodyValue" rows="1" auto-grow max-rows="12" hide-details
                                        placeholder="Thêm ghi chú ở đây"></v-textarea>
                                </div>
                            </div>
                        </div>
                    </v-expansion-panel-text>
                </v-expansion-panel>
            </v-expansion-panels>
        </v-card-text>
        <!-- <v-dialog v-model="typingNewNote" scrollable persistent contained fullscreen >
            <v-card>
                <v-card-title class="text-h6 pa-1">Nội dung ghi chú:</v-card-title>
                <v-divider></v-divider>
                <v-card-text style="max-height: 348px; height: min-content" class="pa-1">
                    <v-textarea variant="outlined" v-model="tempt"  :rules="[rules.require]" :readonly="addingNewNote"
                        rows="6" auto-grow max-rows="12"  placeholder="Nhập nội dung cần ghi chú ở đây"
                    ></v-textarea>
                </v-card-text>
                <v-divider></v-divider>
                <v-card-actions class="d-flex justify-end">
                    <v-btn color="blue-darken-1" variant="text" @click="{typingNewNote = false; tempt = ''}" :disabled="addingNewNote">
                        Huỷ
                    </v-btn>
                    <v-btn color="blue-darken-1" variant="text" @click="addNewNote" :loading="addingNewNote">
                        Lưu
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog> -->
    </v-card>
</template>

<style >
.set-min-height * {
    min-height: min-content !important
}

.annotation-selected {
    background-color: cornsilk;
}

.v-textarea .v-field__input {
    -webkit-mask-image: none !important;
    padding: 2px 4px;
}

.annotation-container {
    min-height: 60px;
    background-color: #fff;
}

.v-expansion-panel-title {
    /* border-bottom: 1px solid rgba(0, 0, 0, 0.2); */
    /* border-top: 1px solid rgba(0, 0, 0, 0.1); */
}

.v-expansion-panel-title--active>.v-expansion-panel-title__overlay,
.v-expansion-panel-title[aria-haspopup=menu][aria-expanded=true]>.v-expansion-panel-title__overlay {
    opacity: 0.1 !important;
    /* border-bottom: 1px solid rgba(0, 0, 0, 0.3); */
}

.v-text-field .v-input__details {
    padding-inline-start: 0;
    padding-inline-end: 0
}
</style>

<script setup>
import { useAnnotationStore } from '@/store/annotation';
import { storeToRefs } from 'pinia'
import { ref, onBeforeUnmount } from 'vue'

const props = defineProps({
    annotationManager: Object
})

const annotationStore = useAnnotationStore()
const { annotationObj, annotationObjView, expansions, annotationSelected, typingNewNote, pdfPageHeight, pdfPageWidth } = storeToRefs(annotationStore)
const lookupAnnotation = {
    note: "GHI CHÚ",
    strikeout: "GẠCH NGANG",
    underline: "GẠCH DƯỚI",
    highlight: "HIGHLIGHT"
}

const tempt = ref("")
// const addingNewNote = ref(false)
// watch(typingNewNote, () => {
//     if(addingNewNote.value && !typingNewNote.value) {
//         addingNewNote.value = false
//     }
// })
const rules = {
    require: value => value.length > 0 || "Vui lòng nhập nội dung cần ghi chú hoặc chọn Huỷ !",
}

// async function addNewNote() {
//     addingNewNote.value = true
//     setTimeout(() => addingNewNote.value = false, 2000)
//     let result = await annotationStore.addNewNote(tempt.value)
//     if(result) {
//         tempt.value = ""
//     }
// }

function getHeaderAnnotation(bodyValue, subtype) {
    if (subtype.length === 0)
        return "[ Đã có lỗi xảy ra ! ]"
    if (bodyValue.length === 0 || subtype === 'note') {
        return lookupAnnotation[subtype]
    } else {
        return lookupAnnotation[subtype] + ", GHI CHÚ"
    }
}

function updateAnnotation(e, pageNumber, idx) {
    if (e === false && JSON.stringify(annotationObjView.value[pageNumber][idx]) !== JSON.stringify(annotationObj.value[pageNumber][idx])) {
        console.log("updateAnnotation", annotationObjView.value[pageNumber][idx], annotationObj.value[pageNumber][idx])
        // annotationStore.updateAnnotation(pageNumber, idx)
        const updatedAnnotation = JSON.parse(JSON.stringify(annotationObjView.value[pageNumber][idx]))
        // console.log("updatedAnnotation", updatedAnnotation)
        // console.log("updateAnnotation",updatedAnnotation)
        props.annotationManager.updateAnnotation(updatedAnnotation)
            .then(() => {
                // goi API update Annotation
                console.log("updateAnnotation:  success")
                props.annotationManager.unselectAnnotation()
                    .then(() => setTimeout(() => gotoAnnotation(updatedAnnotation.id), 0))
                    .catch(error => console.log(error));

            })
            .catch(error => console.log(error));
    }
}

function deleteAnnotation(annotation, idx) {
    props.annotationManager.unselectAnnotation()
        .then(() => {
            props.annotationManager.deleteAnnotations({ annotationIds: [annotation.id] })
                .then(() => {
                    // annotationStore.deleteAnnotation(annotation, idx)
                })
                .catch(error => console.log(error));
        })
        .catch(error => console.log(error));

}

function gotoAnnotation(id) {
    props.annotationManager.selectAnnotation(id)
        .then() //() => console.log("Success")
        .catch(error => console.log(error));
}

function onFocus(id) {
    setTimeout(() => {
        const elm = document.getElementById(id)?.querySelector(`textarea[class="v-field__input"]`)
        if (elm) {
            elm.focus()
        }
    }, 50)
    // document.getElementById(id)?.querySelector(`textarea[class="v-field__input"]`)?.focus()
}
</script>