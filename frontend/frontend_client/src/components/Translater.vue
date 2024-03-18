<template>
    <v-card class="mx-auto">
        <v-card-title class="text-body-1 ma-0 pa-0 mx-4 d-flex justify-space-between align-center">
            Nội dung cần dịch: {{ translationStore.currentLanguage }}
            <v-icon color="#00ADEF" @click="translationStore.speakText(0)"
                v-show="urlAudios.length && !isSpeakingText">mdi-volume-high</v-icon>
            <v-icon color="#00ADEF" @click="translationStore.pauseAudioText"
                v-show="urlAudios.length && isSpeakingText">mdi-square-rounded</v-icon>
            <!-- <v-btn @click="speakText(0)" v-show="urlAudios.length">Speak</v-btn> -->
        </v-card-title>
        <v-card-item class="py-0">
            <v-textarea hint hide-details maxlength="6000" variant="outlined" v-model="needTranslatedText" placeholder="Nhập vào đây nội dung cần dịch..."
                :rows="(needTranslatedText.length > 250) ? '9' : '6'" max-rows="9" auto-grow></v-textarea>
            <v-btn variant="text" density="compact" @click="translationStore.translate"
                v-show="translatedText.length === 0 && needTranslatedText.length > 0">Dịch
                ngay</v-btn>
            <div class="d-inline-block float-right text-body-2 pt-1 text-medium-emphasis"> {{
                needTranslatedText.length }} / 6000 </div>
        </v-card-item>
    </v-card>
    <v-card flat class="mx-auto mb-4" max-width="344">
        <v-card-title class="text-body-1">
            Dịch sang:
            <select id="selectCurrentLang" v-model="targetLanguage">
                <option v-for="(lang, i) in langsArr" :value="lang" :key="i">{{ lang }}</option>
            </select>
            <!-- <v-combobox density="compact"
                                        :items="langsArr" class="d-inline-block"
                                        variant="outlined"></v-combobox> -->
        </v-card-title>
        <v-card-item class="py-0">
            <v-textarea hide-details hint variant="outlined" :rows="(needTranslatedText.length > 250) ? '9' : '6'"
                v-model="translatedText" readonly auto-grow max-rows="9"></v-textarea>
        </v-card-item>
    </v-card>
    <v-overlay contained v-model="loading" persistent class="align-center justify-center">
        <v-progress-circular color="#00ADEF" indeterminate size="64"></v-progress-circular>
    </v-overlay>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { languagesObj, languagesArr } from "../etc/languages";
import { useTranlationStore } from '@/store/translation';
import { storeToRefs } from 'pinia'
import TranslateService from '../services/translate.service'

const langsArr = languagesArr;
const translationStore = useTranlationStore()
const { urlAudios, isSpeakingText, needTranslatedText, translatedText, loading, targetLanguage } = storeToRefs(translationStore)


</script>