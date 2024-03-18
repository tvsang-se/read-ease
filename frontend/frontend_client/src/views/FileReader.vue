<template>
  <div class="c1" v-show="!loading">
    <div class="c2">
      <div class="c3">
        <div id="adobe-dc-view"></div>
      </div>
    </div>
    <div class="right-side-bar ">
      <div class="nav-bar">
        <div class="text-center d-flex flex-column align-center mt-2">
          <v-menu open-on-hover location="left">
            <template v-slot:activator="{ props: menu }">
              <v-icon v-bind="menu" color="#00ADEF" size="xx-large" class="mb-3"
                icon="mdi-dots-horizontal-circle-outline"></v-icon>

            </template>
            <v-list density="compact" class="mr-4">
              <v-list-item>
                <v-list-item-title>
                  <v-tooltip location="left">
                    <template v-slot:activator="{ props }">
                      <span v-bind="props">{{ (pdfTitle.length > 35) ? (pdfTitle.slice(0, 33) + "...")
    : (pdfTitle) }}</span>
                    </template>
                    <span>{{ pdfTitle }}</span>
                  </v-tooltip>
                </v-list-item-title>
              </v-list-item>
              <v-list-item @click="download">
                <v-list-item-title>
                  <v-icon color="#00ADEF">mdi-tray-arrow-down</v-icon>
                  Tải xuống
                </v-list-item-title>
              </v-list-item>
              <v-list-item @click="print">
                <v-list-item-title>
                  <v-icon color="#00ADEF">mdi-printer-outline</v-icon>
                  In tập tin
                </v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>

          <v-menu :close-on-content-click="false" open-on-hover open-on-click location="left">
            <template v-slot:activator="{ props }">
              <v-icon v-bind="props" color="#00ADEF" size="xx-large" icon="mdi-file-search-outline"></v-icon>
            </template>
            <v-card min-width="320" class="ma-0 pa-0 mr-4">
              <v-text-field density="compact" variant="solo" placeholder="Tìm trong tài liệu..."
                prepend-inner-icon="mdi-magnify" single-line hide-details clearable @click:prepend-inner="search"
                v-model="searchText" @click:clear="clearSearchResult" @keyup.enter="search">

                <template v-slot:append>
                  <span v-show="searchObject?.previous">{{ currentResultIndex }} / {{
    totalResults }}</span>
                  <v-icon size="medium" @click="previousSearchResult" class="ml-3">mdi-less-than</v-icon>
                  <v-icon size="medium" @click="nextSearchResult" class="ml-2 mr-3">mdi-greater-than</v-icon>
                </template>
              </v-text-field>
            </v-card>
          </v-menu>

          <!-- <div class="pt-4 text-center">
            <v-dialog v-model="mindMapDialog" transition="dialog-bottom-transition" width="1200px" height="690px" persistent>
              <template v-slot:activator="{ props: activatorProps }">
                <v-icon v-bind="activatorProps" color="#00ADEF" size="xx-large" icon="mdi-sitemap-outline"></v-icon>
              </template>

              <v-toolbar>
                <v-btn icon="mdi-close" @click="mindMapDialog = false"></v-btn>

                <v-toolbar-title>Sơ đồ tư duy</v-toolbar-title>

                <v-spacer></v-spacer>

                <v-toolbar-items>
                  <v-btn text="Lưu" variant="text" @click="mindMapDialog = false"></v-btn>
                </v-toolbar-items>
              </v-toolbar>

              <iframe @load="setLocalStorage" id="iFrameId" src="http://localhost:7998/#/" width="100%"
                    height="100%" frameborder="0"></iframe>
            </v-dialog>
          </div> -->

          <v-tabs class="mt-2" v-model="currentTab" direction="vertical" color="#00ADEF" mandatory="false">
            <v-tab value="1">
              <v-tooltip location="left">
                <template v-slot:activator="{ props }">
                  <v-icon v-bind="props" color="#00ADEF" size="xx-large"
                    icon="mdi-clipboard-text-multiple-outline"></v-icon>
                </template>
                <span>Quản lý chú thích</span>
              </v-tooltip>
            </v-tab>

            <v-tab value="2" v-show="bookmarks.length">
              <v-tooltip location="left">
                <template v-slot:activator="{ props }">
                  <v-icon v-bind="props" color="#00ADEF" size="xx-large" icon="mdi-bookmark-multiple"></v-icon>
                </template>
                <span>Xem đánh dấu trang</span>
              </v-tooltip>
            </v-tab>

            <v-tab value="3">
              <v-tooltip location="left">
                <template v-slot:activator="{ props }">
                  <v-badge dot v-if="translationStore.showBagesTranslateIcon" color="warning">
                    <v-icon v-bind="props" color="#00ADEF" size="xx-large" icon="mdi-translate"></v-icon>
                  </v-badge>
                  <v-icon v-else v-bind="props" color="#00ADEF" size="xx-large" icon="mdi-translate"></v-icon>
                </template>
                <span>Dịch từ hoặc cụm từ</span>
              </v-tooltip>
            </v-tab>

            <!-- <v-tab value="4">
              <v-tooltip location="left">
                <template v-slot:activator="{ props }">
                  <v-badge dot v-if="translationStore.showBagesTranslateIcon" color="warning">
                                        <v-icon v-bind="props" color="#00ADEF" size="xx-large"
                                            icon="mdi-sitemap-outline"></v-icon>
                                    </v-badge>

                  <div class="pa-4 text-center">
                    <v-dialog v-model="mindMapDialog" max-width="600">
                      <template v-slot:activator="{ props: activatorProps }">
                        <v-btn class="text-none font-weight-regular" prepend-icon="mdi-account" text="Edit Profile"
                          variant="tonal" v-bind="activatorProps"></v-btn>
                          <v-icon v-bind="[props, activatorProps]" color="#00ADEF" size="xx-large" icon="mdi-sitemap-outline"></v-icon>
                      </template>

                      <v-card prepend-icon="mdi-account" title="User Profile">
                        <v-card-text>
                          <v-row dense>
                            <v-col cols="12" md="4" sm="6">
                              <v-text-field label="First name*" required></v-text-field>
                            </v-col>

                            <v-col cols="12" md="4" sm="6">
                              <v-text-field hint="example of helper text only on focus"
                                label="Middle name"></v-text-field>
                            </v-col>

                            <v-col cols="12" md="4" sm="6">
                              <v-text-field hint="example of persistent helper text" label="Last name*" persistent-hint
                                required></v-text-field>
                            </v-col>

                            <v-col cols="12" md="4" sm="6">
                              <v-text-field label="Email*" required></v-text-field>
                            </v-col>

                            <v-col cols="12" md="4" sm="6">
                              <v-text-field label="Password*" type="password" required></v-text-field>
                            </v-col>

                            <v-col cols="12" md="4" sm="6">
                              <v-text-field label="Confirm Password*" type="password" required></v-text-field>
                            </v-col>

                            <v-col cols="12" sm="6">
                              <v-select :items="['0-17', '18-29', '30-54', '54+']" label="Age*" required></v-select>
                            </v-col>

                            <v-col cols="12" sm="6">
                              <v-autocomplete
                                :items="['Skiing', 'Ice hockey', 'Soccer', 'Basketball', 'Hockey', 'Reading', 'Writing', 'Coding', 'Basejump']"
                                label="Interests" auto-select-first multiple></v-autocomplete>
                            </v-col>
                          </v-row>

                          <small class="text-caption text-medium-emphasis">*indicates required field</small>
                        </v-card-text>

                        <v-divider></v-divider>

                        <v-card-actions>
                          <v-spacer></v-spacer>

                          <v-btn text="Close" variant="plain" @click="dialog = false"></v-btn>

                          <v-btn color="primary" text="Save" variant="tonal" @click="dialog = false"></v-btn>
                        </v-card-actions>
                      </v-card>
                    </v-dialog>
                  </div>



                </template>
                <span>Ve so do tu duy</span>
              </v-tooltip>
            </v-tab> -->
          </v-tabs>
        </div>
        <div class="nav-bar-bottom">
          <v-tooltip location="left" max-width="220px">
            <template v-slot:activator="{ props }">
              <v-text-field v-bind="props" density="compact" rounded="3px" type="number" v-model="currentPage"
                variant="outlined" @keyup.enter="gotoPage" :max="numPages" :min="1"
                :rules="[rules.valid]"></v-text-field>
            </template>
            <span>Trang hiện tại hoặc nhập vào trang bạn muốn đến</span>
          </v-tooltip>
          <v-tooltip location="left">
            <template v-slot:activator="{ props }">
              <div v-bind="props" class="text-center mb-0">
                {{ numPages }}
              </div>
            </template>
            <span>Tổng số trang của tập tin PDF này</span>
          </v-tooltip>
          <v-tooltip location="left">
            <template v-slot:activator="{ props }">
              <v-icon @click="previousPage" v-bind="props" color="#00ADEF" size="xx-large"
                icon="mdi-chevron-up"></v-icon>
            </template>
            <span>Trở lại trang trước (^)</span>
          </v-tooltip>
          <v-tooltip location="left">
            <template v-slot:activator="{ props }">
              <v-icon v-bind="props" @click="nextPage" color="#00ADEF" size="xx-large" icon="mdi-chevron-down"
                class="my-0"></v-icon>
            </template>
            <span>Đi đến trang sau (v)</span>
          </v-tooltip>
          <v-tooltip location="left">
            <template v-slot:activator="{ props }">
              <v-icon @click="zoomIn" v-bind="props" color="#00ADEF" size="xx-large"
                icon="mdi-magnify-plus-outline"></v-icon>
            </template>
            <span>Phóng to (Ctrl +)</span>
          </v-tooltip>
          <v-tooltip location="left">
            <template v-slot:activator="{ props }">
              <v-icon @click="zoomOut" v-bind="props" color="#00ADEF" size="xx-large"
                icon="mdi-magnify-minus-outline"></v-icon>
            </template>
            <span>Thu nhỏ (Ctrl -)</span>
          </v-tooltip>
        </div>

      </div>
      <!-- overflow-x-hidden overflow-y-auto -->
      <div class="content" v-show="currentTab > 0" style="border-left: 1px solid #000;">
        <v-card elevation="0" class="mt-2 pb-4" min-height="3.4rem">
          <template v-slot:prepend>
            <v-icon @click="currentTab = -1">mdi-close</v-icon>
          </template>
          <template v-slot:title>
            {{ titleOfRightSideBarTab }}
            <span v-show="currentTab === '1'" class="font-weight-medium text-disabled pl-10 ml-1 text-body-1">{{
    numAnnotations }}</span>
          </template>
          <v-banner lines="one" color="info" v-if="showBanner && currentTab === '3'">
            <template v-slot:text>
              Dịch đoạn văn vừa chọn
            </template>

            <template v-slot:actions>
              <v-btn @click="translationStore.translateNewSelectedText">
                OK
              </v-btn>

              <v-btn @click="showBanner = false">
                Huỷ
              </v-btn>
            </template>
          </v-banner>
        </v-card>
        <div class="content-text">
          <v-window v-model="currentTab" mandatory="false">
            <v-window-item value="1">
              <AnnotationManager :annotationManager="annotationManager" v-if="Object.keys(annotationObj).length">
              </AnnotationManager>
              <div v-else class="text-center">
                <span class="font-weight-regular text-high-emphasis">Chưa có chú thích nào !</span>
              </div>
            </v-window-item>
            <v-window-item value="2" v-show="bookmarks.length">
              <v-card flat>
                <v-card-text class="pt-0">
                  <TableOfContent :bookmarks="bookmarks" :viewerApis="viewerApis" :currentIndex="parseInt(0)"
                    v-if="bookmarks && viewerApis"></TableOfContent>
                </v-card-text>
              </v-card>
            </v-window-item>
            <v-window-item value="3" max-width="344">
              <Translater></Translater>
            </v-window-item>
          </v-window>
        </div>
      </div>
    </div>
  </div>
  <div class="c1 bg-blue-lighten-5" v-if="loading">
    <span class="loader"></span>
  </div>
</template>

<style>
#loadingMessage {
    width: 100%;
    height: 100%;
    z-index: 1000;
    background: #ccc;
    top: 0px;
    left: 0px;
    position: absolute;
}
.loader {
  display: block;
  width: 200px;
  height: 140px;
  /* background: #979794; */
  background: #00ADEF;
  box-sizing: border-box;
  position: relative;
  border-radius: 8px;
  perspective: 1000px;
}

.loader:before {
  content: "";
  position: absolute;
  left: 10px;
  right: 10px;
  top: 10px;
  bottom: 10px;
  border-radius: 8px;
  background: #f5f5f5 no-repeat;
  background-size: 60px 10px;
  background-image: linear-gradient(#ddd 100px, transparent 0),
    linear-gradient(#ddd 100px, transparent 0),
    linear-gradient(#ddd 100px, transparent 0),
    linear-gradient(#ddd 100px, transparent 0),
    linear-gradient(#ddd 100px, transparent 0),
    linear-gradient(#ddd 100px, transparent 0);

  background-position: 15px 30px, 15px 60px, 15px 90px, 105px 30px, 105px 60px,
    105px 90px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.25);
}

.loader:after {
  content: "";
  position: absolute;
  width: calc(50% - 10px);
  right: 10px;
  top: 10px;
  bottom: 10px;
  border-radius: 8px;
  background: #fff no-repeat;
  background-size: 60px 10px;
  background-image: linear-gradient(#ddd 100px, transparent 0),
    linear-gradient(#ddd 100px, transparent 0),
    linear-gradient(#ddd 100px, transparent 0);
  background-position: 50% 30px, 50% 60px, 50% 90px;
  transform: rotateY(0deg);
  transform-origin: left center;
  animation: paging 1s linear infinite;
}

@keyframes paging {
  to {
    transform: rotateY(-180deg);
  }
}

input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

input[type=number] {
  -moz-appearance: textfield;
  text-align: center;
  padding-left: 0;
  padding-right: 0;
}

.v-list-item {
  cursor: pointer;
}

.translate-icon {
  border-radius: 3px;
  /* padding: 1px; */
  animation: zoom-in-zoom-out-and-change-background-color 0.69s ease 6
}

@keyframes zoom-in-zoom-out-and-change-background-color {
  0% {
    transform: scale(1, 1);
    background-color: #fff;
  }

  25% {
    transform: scale(1.15, 1.15);
    background-color: rgba(236, 230, 150, 0.5);
  }

  50% {
    transform: scale(1.3, 1.3);
    background-color: rgb(236, 230, 150);
  }

  75% {
    transform: scale(1.15, 1.15);
    background-color: rgba(236, 230, 150, 0.5);
  }

  100% {
    transform: scale(1, 1);
    background-color: #fff;
  }
}

.content {
  border-right: 1px solid black;
  display: flex;
  flex-direction: column;
  min-width: 330px;
}

.content-text {
  flex-grow: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.v-banner-text {
  padding-right: 0 !important;
}

.c1 {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  /* padding: 15px; */
  /* padding-left: 48px; */
}

.c2 {
  background-color: brown;
  width: 100%;
  height: 100%;
  /* padding: 10px; */
  /* overflow: hidden; */
}

.right-side-bar {
  width: fit-content;
  max-width: 390px;
  height: 100%;
  display: flex;
  flex-direction: row-reverse;
  justify-content: end;
}

.v-card {
  border-radius: 0 !important;
}

.nav-bar {
  width: fit-content;
  height: 100%;
  background-color: #fff;
  padding: 10px 5px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
}

.nav-bar * {
  cursor: pointer;
}

.nav-bar .v-input {
  max-width: 46px !important;
}

.v-tab.v-tab {
  min-width: min-content !important;
  padding-top: -5px;
}

.v-tabs * {
  text-align: center;
}

.v-tab--selected {
  background-color: #c9e5f0 !important;
  border: none !important;
}

.v-btn--size-default {
  padding: 0 8px;
}

.c3 {
  background-color: burlywood;
  /* height: calc(100% + 40px);
    margin-top: -40px */
  /* padding: 10px; */
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.nav-bar-bottom {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.nav-bar-bottom>i {
  margin-top: 8px;
  margin-bottom: 8px;
}

#adobe-dc-view {
  height: calc(100% + 46.4px);
  width: calc(100% + 50px);
  margin-top: -46.1px;
}

#selectCurrentLang {
  font-size: 15px;
  border: 1px solid black;
  border-radius: 3px;
  text-align: center;
}

#iFrameId {
  background: url(iloader.gif) center center no-repeat;
}
</style>

<script setup>
import { annotations } from '../etc/annotationList'
import TableOfContent from '../components/TableOfContent.vue';
import AnnotationManager from '@/components/AnnotationManager.vue';
import Translater from '@/components/Translater.vue';
import { useTranlationStore } from '@/store/translation';
import { useAnnotationStore } from '@/store/annotation';
import DriveService from '../services/drive.service';
import FileService from '../services/file.service';
import AnnotationService from '@/services/annotation.service';
import { useUserStore } from '../store/user'
import { storeToRefs } from 'pinia'
import { nextTick } from 'vue'
import { ref, onMounted, computed, watch, onBeforeUnmount } from 'vue'
import { useAlertStore } from '@/store/alert';
import { useRouter, useRoute } from 'vue-router';
import { info } from 'pdfjs-dist/build/pdf';

const router = useRouter()
const route = useRoute()

// Adobe Pdf Embed API
let adobeApiPDFReady = ref(false),
  adobeDCView = null,
  previewFilePromise = null,

  viewerApis = null,
  annotationManager = null,
  loading = ref(true),
  numAnnotations = ref(0),

  infoFile = {},
  pdfTitle = ref(""),
  contentPdfFile = null,
  currentPage = ref(0),
  numPages = ref(0),
  bookmarks = ref([]),
  lastReadPage = ref(1),
  dataPagesViewInChange = {},
  dataPreviewPageViewScrolled = {},
  initZoomLevel = -1,
  dataPreviewSelectionEnd = {},

  searchText = ref(""),
  searchObject = null,
  currentResultIndex = ref(-1),
  totalResults = ref(-1),
  addAnnotationsFromDB = true,
  file = null,
  mindMapDialog = ref(false)

// let currentTab = ref(-1)

const rules = {
  valid: value => (value >= 1 && (numPages.value === 0 ? (value >= 1) : (value <= numPages.value))) || "",
}

const titleOfRightSideBarTab = computed(() => {
  const titles = [`Quản lý chú thích `, "Mục lục", "Dịch ngôn ngữ"];
  return titles[currentTab.value - 1]
})

const userStore = useUserStore()
const { user } = storeToRefs(userStore)

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

function setLocalStorage() {
  var win = document.getElementById('iFrameId').contentWindow;
  // win.postMessage({
    //   data: {"root":{"data":{"text":"<p><span style=\"color: rgb(51, 51, 51); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 24px; font-weight: bold; font-style: normal; text-decoration: none;\">People</span></p>","expand":true,"richText":true,"isActive":false,"uid":"f6540871-24cc-42d8-b6b2-c9300a3523bd","image":"https://cdn-icons-png.flaticon.com/512/25/25231.png","imageTitle":"","imageSize":{"width":512,"height":512,"custom":false},"icon":["priority_1"]},"children":[{"data":{"text":"<p><span style=\"color: rgb(51, 51, 51); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 16px; font-style: normal; text-decoration: none;\">node 1</span></p>","uid":"df6bd1aa-cbe2-4151-a3e2-1b3f1442ff34","expand":true,"richText":true,"isActive":false,"image":null,"imageTitle":"","imageSize":{"custom":false},"hyperlink":"https://youtube.com","hyperlinkTitle":""},"children":[{"data":{"text":"<p><span style=\"color: rgb(255, 255, 255); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 12px; font-weight: bold; font-style: normal; text-decoration: none;\">node 1.1</span></p>","uid":"4cd13fcc-a774-47ab-a118-49e6a914b213","expand":true,"richText":true,"isActive":false,"tag":[],"generalization":[{"text":"<p><span style=\"color: rgb(51, 51, 51); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 16px; font-style: normal; text-decoration: none;\">Tóm tắt</span></p>","uid":"3f36cfb3-19d9-4b7d-8f94-805636aedb7d","expand":true,"isActive":false,"richText":true}]},"children":[{"data":{"text":"<p><span style=\"color: rgb(255, 255, 255); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 12px; font-weight: bold; font-style: normal; text-decoration: none;\">node 1.1.1</span></p>","uid":"75ab7da9-4d58-45d0-b5a0-710b056febd2","expand":true,"richText":true,"isActive":false,"image":null,"imageTitle":"","imageSize":{"custom":false}},"children":[]},{"data":{"text":"<p><span style=\"color: rgb(255, 255, 255); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 12px; font-weight: bold; font-style: normal; text-decoration: none;\">node 1.1.2</span></p>","uid":"a4ec0269-b428-405c-9bd3-6a1abea8fe3e","expand":true,"richText":true,"isActive":false},"children":[]}]}]},{"data":{"text":"<p><span style=\"font-size: 16px; font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; color: rgb(51, 51, 51);\">node 2.1</span></p>","expand":true,"richText":true,"isActive":false,"uid":"7f123a39-fd75-4cd4-af4e-f98d1983d00d","icon":["star_8"],"associativeLineTargets":[],"associativeLineTargetControlOffsets":[],"associativeLinePoint":[],"associativeLineText":{}},"children":[{"data":{"text":"<p><span style=\"color: rgb(255, 255, 255); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 12px; font-weight: bold; font-style: normal; text-decoration: none;\">node 2.1</span></p>","uid":"7e0b5ac8-6481-4a8f-a640-4f37e9553051","expand":true,"richText":true,"isActive":false},"children":[]},{"data":{"text":"<p><span style=\"color: rgb(255, 255, 255); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 12px; font-weight: bold; font-style: normal; text-decoration: none;\">node 2.2</span></p>","expand":true,"richText":true,"isActive":false,"uid":"4c8577b9-a817-4f7d-8844-7c6c33eeec6f","generalization":[]},"children":[{"data":{"text":"<p><span style=\"color: rgb(255, 255, 255); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 12px; font-weight: bold; font-style: normal; text-decoration: none;\">node 2.2.1</span></p>","uid":"9346b7ea-c8ef-419b-bd50-ee90e8928fc3","expand":true,"richText":true,"isActive":false},"children":[]},{"data":{"text":"<p><span style=\"color: rgb(255, 255, 255); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 12px; font-weight: bold; font-style: normal; text-decoration: none;\">node 2.2.2</span></p>","expand":true,"richText":true,"isActive":false,"uid":"2206d0c7-4091-4fd8-ace4-ed7d8c91a62a"},"children":[]}]}]},{"data":{"text":"<p><span style=\"color: rgb(51, 51, 51); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 16px; font-style: normal; text-decoration: none;\">node 3</span></p>","expand":true,"richText":true,"isActive":false,"uid":"3e831317-eead-4ba3-bcfb-32a7b498569a","image":null,"imageTitle":"","imageSize":{"custom":false},"tag":["#ctu"],"generalization":[]},"children":[{"data":{"text":"<p><strong style=\"color: rgb(255, 255, 255); font-family: sans-serif; font-size: 12px;\">Công thức</strong><strong style=\"color: rgb(255, 255, 255); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 12px;\"> : <span class=\"ql-formula\" data-value=\"a^2 + b^2 = c^2\">﻿<span contenteditable=\"false\"><span class=\"katex\"><math xmlns=\"http://www.w3.org/1998/Math/MathML\"><semantics><mrow><msup><mi>a</mi><mn>2</mn></msup><mo>+</mo><msup><mi>b</mi><mn>2</mn></msup><mo>=</mo><msup><mi>c</mi><mn>2</mn></msup></mrow><annotation encoding=\"application/x-tex\">a^2 + b^2 = c^2</annotation></semantics></math></span></span>﻿</span></strong></p>","uid":"0f733f9c-0907-4ba5-8857-d8147c1b79af","expand":true,"richText":true,"isActive":false,"fontFamily":"sans-serif","color":"#FFFFFF","fontWeight":"bold"},"children":[]}]}]},"theme":{"template":"classic","config":{}},"layout":"logicalStructure","config":{},"view":{"transform":{"scaleX":1,"scaleY":1,"shear":0,"rotate":0,"translateX":-601,"translateY":-49,"originX":0,"originY":0,"a":1,"b":0,"c":0,"d":1,"e":-601,"f":-49},"state":{"scale":1,"x":-601,"y":-49,"sx":-706,"sy":-34}}},
    //   localConfig: {"isZenMode":false,"openNodeRichText":true,"useLeftKeySelectionRightKeyDrag":false,"isShowScrollbar":false,"isUseHandDrawnLikeStyle":false,"isDark":true},
    //   lang: "vi"
    // }, "*");
    win.postMessage({
      "data": {"layout":"logicalStructure","root":{"data":{"text":"<p><span style=\"color: rgb(255, 255, 255); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 16px; font-weight: bold; font-style: normal; text-decoration: none;\">People</span></p>","expand":true,"richText":true,"isActive":false,"uid":"f6540871-24cc-42d8-b6b2-c9300a3523bd","image":"https://cdn-icons-png.flaticon.com/512/25/25231.png","imageTitle":"","imageSize":{"width":512,"height":512,"custom":false},"icon":["priority_1"]},"children":[{"data":{"text":"<p><span style=\"color: rgb(34, 34, 34); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 14px; font-style: normal; text-decoration: none;\">node 1</span></p>","uid":"df6bd1aa-cbe2-4151-a3e2-1b3f1442ff34","expand":true,"richText":true,"isActive":false,"image":null,"imageTitle":"","imageSize":{"custom":false},"hyperlink":"https://youtube.com","hyperlinkTitle":""},"children":[{"data":{"text":"<p><span style=\"color: rgb(34, 34, 34); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 12px; font-style: normal; text-decoration: none;\">node 1.1</span></p>","uid":"4cd13fcc-a774-47ab-a118-49e6a914b213","expand":true,"richText":true,"isActive":false,"tag":[],"generalization":[{"text":"<p><span style=\"color: rgb(255, 255, 255); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 16px; font-style: normal; text-decoration: none;\">Tóm tắt</span></p>","uid":"3f36cfb3-19d9-4b7d-8f94-805636aedb7d","expand":true,"isActive":false,"richText":true}]},"children":[{"data":{"text":"<p><span style=\"color: rgb(34, 34, 34); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 12px; font-style: normal; text-decoration: none;\">node 1.1.1</span></p>","uid":"75ab7da9-4d58-45d0-b5a0-710b056febd2","expand":true,"richText":true,"isActive":false,"image":null,"imageTitle":"","imageSize":{"custom":false}},"children":[]},{"data":{"text":"<p><span style=\"color: rgb(34, 34, 34); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 12px; font-style: normal; text-decoration: none;\">node 1.1.2</span></p>","uid":"a4ec0269-b428-405c-9bd3-6a1abea8fe3e","expand":true,"richText":true,"isActive":false},"children":[]}]}]},{"data":{"text":"<p><span style=\"color: rgb(34, 34, 34); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 14px; font-style: normal; text-decoration: none;\">node 2.1</span></p>","expand":true,"richText":true,"isActive":false,"uid":"7f123a39-fd75-4cd4-af4e-f98d1983d00d","icon":["star_8"],"associativeLineTargets":[],"associativeLineTargetControlOffsets":[],"associativeLinePoint":[],"associativeLineText":{}},"children":[{"data":{"text":"<p><span style=\"color: rgb(34, 34, 34); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 12px; font-style: normal; text-decoration: none;\">node 2.1</span></p>","uid":"7e0b5ac8-6481-4a8f-a640-4f37e9553051","expand":true,"richText":true,"isActive":false},"children":[]},{"data":{"text":"<p><span style=\"color: rgb(34, 34, 34); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 12px; font-style: normal; text-decoration: none;\">node 2.2</span></p>","expand":true,"richText":true,"isActive":false,"uid":"4c8577b9-a817-4f7d-8844-7c6c33eeec6f","generalization":[]},"children":[{"data":{"text":"<p><span style=\"color: rgb(34, 34, 34); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 12px; font-style: normal; text-decoration: none;\">node 2.2.1</span></p>","uid":"9346b7ea-c8ef-419b-bd50-ee90e8928fc3","expand":true,"richText":true,"isActive":false},"children":[]},{"data":{"text":"<p><span style=\"color: rgb(34, 34, 34); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 12px; font-style: normal; text-decoration: none;\">node 2.2.2</span></p>","expand":true,"richText":true,"isActive":false,"uid":"2206d0c7-4091-4fd8-ace4-ed7d8c91a62a"},"children":[]}]}]},{"data":{"text":"<p><span style=\"color: rgb(34, 34, 34); font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; font-size: 14px; font-style: normal; text-decoration: none;\">node 3</span></p>","expand":true,"richText":true,"isActive":false,"uid":"3e831317-eead-4ba3-bcfb-32a7b498569a","image":null,"imageTitle":"","imageSize":{"custom":false},"tag":["#ctu"],"generalization":[]},"children":[{"data":{"text":"<p><span style=\"font-size: 12px; font-family: sans-serif; color: rgb(51, 51, 51);\">Công thức</span><strong style=\"font-size: 12px; font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; color: rgb(51, 51, 51);\"> </strong><span style=\"font-size: 12px; font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; color: rgb(51, 51, 51);\">:</span><strong style=\"font-size: 12px; font-family: 微软雅黑, &quot;Microsoft YaHei&quot;; color: rgb(51, 51, 51);\"> <span class=\"ql-formula\" data-value=\"a^2 + b^2 = c^2\">﻿<span contenteditable=\"false\"><span class=\"katex\"><math xmlns=\"http://www.w3.org/1998/Math/MathML\"><semantics><mrow><msup><mi>a</mi><mn>2</mn></msup><mo>+</mo><msup><mi>b</mi><mn>2</mn></msup><mo>=</mo><msup><mi>c</mi><mn>2</mn></msup></mrow><annotation encoding=\"application/x-tex\">a^2 + b^2 = c^2</annotation></semantics></math></span></span>﻿</span></strong></p>","uid":"0f733f9c-0907-4ba5-8857-d8147c1b79af","expand":true,"richText":true,"isActive":false,"fontFamily":"sans-serif","color":"#333333","fontWeight":"normal"},"children":[]}]}]},"theme":{"template":"mint","config":{}},"view":{"transform":{"scaleX":1.4,"scaleY":1.4,"shear":0,"rotate":0,"translateX":-941,"translateY":-256.0324340820313,"originX":0,"originY":0,"a":1.4,"b":0,"c":0,"d":1.4,"e":-941,"f":-256.0324340820313},"state":{"scale":1.4,"x":-941,"y":-256.0324340820313,"sx":-1064,"sy":-278.0324340820313}}}, "localConfig": { "isZenMode": false, "openNodeRichText": true, "useLeftKeySelectionRightKeyDrag": false, "isShowScrollbar": false, "isUseHandDrawnLikeStyle": false, "isDark": false }, "lang": "vi"
    }, "*");
}

async function getFile() {
  try {
    const id = route.params?.id
    if (!id) {
      router.push({ name: "NotFound" })
    }
    const res = await FileService.getFile(id)
    // console.log(res)
    infoFile = { ...res.data.document }
    let t = []
    for (let i = 0; i < res.data.annotations.length; i++) {
      t.push({
        ...res.data.annotations[i],
        '@context': res.data.annotations[i].context
      })
    }
    infoFile.annotations = [...t]
    pdfTitle.value = infoFile.name
    numPages.value = infoFile.totalPages
    if (infoFile.numberOfPagesReading)
      lastReadPage.value = infoFile.numberOfPagesReading
    else
      lastReadPage.value = 1
    getFileFromGGDrive()
  } catch (error) {
    console.log(error)
    useAlertStore().showCommonError("")
  }
}

async function getFileFromGGDrive() {
  try {
    if (!user.value?.accessTokenGGDrive) {
      await getAccessTokenGoogle()
    }
    let tempt = user.value.accessTokenGGDrive.split('')
    const t = tempt[6]
    tempt[6] = tempt[9]
    tempt[9] = t
    const res = await DriveService.getFileFromGGDrive(infoFile.url, tempt.join(''))
    // console.log(res)
    file = res.data
    var filePromise = Promise.resolve(res.data)
    openPdfFile(filePromise)
  } catch (error) {
    if (error?.response?.status === 401) {
      await getAccessTokenGoogle()
      getFileFromGGDrive()
    } else {
      console.log(error)
      useAlertStore().showCommonError("")
    }
  }
}

function addAdobeViewSdk() {
  if (document.getElementById('src-adobe-view-sdk')) {
    // window.location.reload()
    adobeApiPDFReady.value = true;
    return
  }
  document.addEventListener("adobe_dc_view_sdk.ready", () => {
    adobeApiPDFReady.value = true;
    console.log("Adobe created with adobe_dc_view_sdk.ready");
  });
  // Dynamically load Adobe SDK Viewer for this page
  const plugin = document.createElement("script");
  plugin.setAttribute('id', 'src-adobe-view-sdk')
  plugin.setAttribute("src", "https://acrobatservices.adobe.com/view-sdk/viewer.js");
  plugin.async = true;
  document.head.appendChild(plugin);
}

async function openPdfFile(filePromise) {
  // console.log("openPdfFile")
  const viewerConfig = {
    enableAnnotationAPIs: true,
    includePDFAnnotations: true,
    // enableLinearization: true,
    defaultViewMode: "FIT_WIDTH",
    enableSearchAPIs: true,
  }
  // console.log(filePromise)
  previewFilePromise = adobeDCView.previewFile({
    content: {
      promise: filePromise
      // location: {
      //     url:
      //         // "https://documentcloud.adobe.com/view-sdk-demo/PDFs/Bodea Brochure.pdf",
      //         // "./You Don't Know JS_ Scope & Closures.pdf"
      //         "./Code Dạo Ký Sự.pdf"
      //     // "https://www.googleapis.com/drive/v3/files/17AtBUWQ9Arhhwn-Mw5J2H-TYYTVEq62A?alt=media",//export?mimeType=pdf",
      //     // headers: [
      //     //     { Authorization: "Bearer ya29.a0AfB_byCr4odi2YmdlCXAwhelXoBwMfptpMu8H2fd2Yw3qiPrdPv7bi1R1ly0AymKS3ms6OqGORkQfWbjQ_QdN44wTDUeI5PU_A9ZmqQewwJgKeaXJSZoyckPqvsspz1Ar-p2s9HTn0iJ6T74rpoNumdA6DGWmCoSyq90YgaCgYKAWwSARMSFQGOcNnCNK92RKGKTuL0d5xmkLkPAg0173" }
      //     // ]
      // },
    },
    metaData: { fileName: infoFile.name, id: `${infoFile.id}` },
  }, viewerConfig);

  const eventOptions = {
    listenOn: [
      // "APP_RENDERING_START",
      "PREVIEW_SELECTION_END",
      "CONTEXT_MENU_ITEM_CLICKED",
      "CURRENT_ACTIVE_PAGE",
      "PREVIEW_PAGE_CLICK",
      // "APP_RENDERING_DONE",
      "ANNOTATION_ADDED",
      "ANNOTATION_UPDATED",
      "ANNOTATION_DELETED",
      "ANNOTATION_SELECTED",
      "ANNOTATION_UNSELECTED",
      // "PREVIEW_PAGE_VIEW_SCROLLED",
      // "PAGES_IN_VIEW_CHANGE",
      // "PREVIEW_ZOOM",
      "ANNOTATION_COUNT",
    ],
    enableFilePreviewEvents: true,
    enableAnnotationEvents: true,
    enablePDFAnalytics: true
  }

  adobeDCView.registerCallback(AdobeDC.View.Enum.CallbackType.EVENT_LISTENER,
    (async (event) => await handleEvents(event)),
    eventOptions)

  const saveOptions = {
    autoSaveFrequency: 1,
    enableFocusPolling: true,
    showSaveButton: false
  };
  adobeDCView.registerCallback(AdobeDC.View.Enum.CallbackType.SAVE_API, (metaData, content, options) => {
    contentPdfFile = content
    console.log("Saved.");
    /* Add your custom save implementation here...and based on that resolve or reject response in given format */
    return new Promise((resolve, reject) => {
      resolve({
        code: AdobeDC.View.Enum.ApiResponseCode.SUCCESS,
        data: {
          metaData
        }
      });
    });
  }, saveOptions);

  // console.log(adobeDCView)

  await previewFilePromise.then(async (adobeViewer) => {
    adobeViewer.getAPIs().then((apis) => {
      viewerApis = apis
    })

    await adobeViewer.getAnnotationManager().then((annoManager) => {
      annotationManager = annoManager
      /* API to set UI configurations */
      const customFlags = {
        showToolbar: false,   /* Default value is true */
        showCommentsPanel: false,  /* Default value is true */
        downloadWithAnnotations: true,  /* Default value is false */
        printWithAnnotations: true,  /* Default value is false */
      };

      annotationManager.setConfig(customFlags)
        .then(function () {
          console.log("Custom flags applied successfully.")
        })
        .catch(function (error) {
          console.log(error)
        });

      /* API to add annotations */
      if (infoFile.annotations.length)
        annotationManager.addAnnotationsInPDF(infoFile.annotations)
          .then(function () {
            // if (infoFile.annotations.length === 1) {
            //     annotationManager.unselectAnnotation()
            //         .then(() => {
            //             viewerApis.gotoLocation(lastReadPage.value, 0, 1)
            //                 .then(result => currentPage.value = lastReadPage.value)
            //                 .catch(error => console.log(error))
            //         })
            //         .catch(error => console.log(error))
            // }
            formatAnnotations()
            // console.log("Annotations added through API successfully")
          })
          .catch(function (error) {
            console.log(error)
          });

      /* API to get all annotations */
      // annotationManager.getAnnotations()
      //     .then(function (result) {
      //         console.log("GET all annotations", result)
      //     })
      //     .catch(function (error) {
      //         console.log(error)
      //     });
    })
  })
  // console.log(viewerApis)
  viewerApis.getBookmarkAPIs().getBookmarks()
    .then(result => {
      if (result.length > 0)
        bookmarks.value = result
      else bookmarks.value = []
      // console.log(bookmarks.value)
    })
    .catch(error => console.log(error));

  if (lastReadPage.value === 1 && numPages.value > 1) {
    viewerApis.gotoLocation(2)
      .then()
      .catch(error => console.log(error))
  }
  viewerApis.gotoLocation(lastReadPage.value, 0, 1)
    .then(result => { currentPage.value = lastReadPage.value; loading.value = false })
    .catch(error => console.log(error))

  // viewerApis.getPDFMetadata()
  //     .then((result) => {
  //         pdfTitle.value = result.pdfTitle
  //         numPages.value = result.numPages
  //     })
  //     .catch(error => console.log(error))

  // viewerApis.getPageZoom(1)
  //     .then(result => {
  //         initZoomLevel = result;
  //         if (pdfPageHeight.value > 0) {
  //             pdfPageHeight.value /= initZoomLevel
  //             pdfPageWidth.value /= initZoomLevel
  //             console.log("HEIGHT:", pdfPageHeight.value, " , WIDTH: ", pdfPageWidth.value, ", ZOOM LEVEL: ", initZoomLevel)
  //         }
  //     })
  //     .catch(error => console.log(error))




  // console.log(adobeDCView)
  // setTimeout(() => {

  // }, 1000)


}

async function handleEvents(event) {
  // console.log(event)
  if (event.type === "PREVIEW_SELECTION_END") {

    // viewerApis.getPageZoom(parseInt(currentPage.value))
    //     .then(result => {
    //         ;
    //         // alert(result)
    //         dataPreviewSelectionEnd = { ...event.data, zoomLevel: result }
    //         // console.log(dataPreviewSelectionEnd)
    //     })
    //     .catch(error => console.log(error))
    setTimeout(() => viewerApis.getSelectedContent()
      .then(result => {
        selectedText.value = result.data.replace(/\s+/g, ' ').trim();
      })
      .catch(error => console.log(error))
      , 10);
  } else if (event.type === "CURRENT_ACTIVE_PAGE") {
    currentPage.value = event.data.pageNumber
    // loading.value = false
  } else if (event.type === "PREVIEW_PAGE_CLICK") {
    if (addAnnotationsFromDB)
      addAnnotationsFromDB = false
    searchText.value = ""
  } else if (event.type === "APP_RENDERING_DONE") {
    // loading.value = false
    // console.log("Xong")
  } else if (event.type === "ANNOTATION_SELECTED") {
    // console.log(event)
    // alert("ANNOTATION_SELECTED")
    // console.log("ANNOTATION_SELECTED",event.data.id)
    if (addAnnotationsFromDB && infoFile.annotations.length === 1) {
      addAnnotationsFromDB = false
      annotationManager.unselectAnnotation()
        .then(() => {
          viewerApis.gotoLocation(lastReadPage.value, 0, 1)
            .then(result => currentPage.value = lastReadPage.value)
            .catch(error => console.log(error))
          currentTab.value = -1
          annotationSelected.value = {}
        })
        .catch(error => console.log(error))
      return
    }
    if (currentTab.value !== '1') {
      document.querySelector('button.v-tab[value="1"]')?.click()
      await nextTick()
      setTimeout(() => annotationSelected.value = JSON.parse(JSON.stringify(event.data)), 450)
    } else {
      annotationSelected.value = JSON.parse(JSON.stringify(event.data))
    }
  } else if (event.type === "ANNOTATION_UNSELECTED") {
    annotationSelected.value = JSON.parse(JSON.stringify({}))
  } else if (event.type === "ANNOTATION_ADDED") {
    console.log(event)
    if (!addAnnotationsFromDB) {
      // alert("Thêm nè")
      // annotationStore.addedAnnotation(event.data)
      try {
        const res = await AnnotationService.addNewAnnotation(event.data);
        let page = parseInt(event.data.target.selector.node.index) + 1;
        // if (!annotationObj.value[page]) annotationObj.value[page] = [];
        // annotationObj.value[page].push(annotation);
        await annotationManager.getAnnotations({
          pageRange: { startPage: page, endPage: page }
        })
          .then((result) => {
            if (result.length) {
              annotationObj.value[page] = result
            }
          })
          .catch(error => console.log(error))
        // await nextTick();
        // const elm = document.getElementById(annotation.id);
        // if (elm) {
        //   elm.click();
        //   elm.querySelector(`textarea[class="v-field__input"]`)?.focus();
        // }
      } catch (error) {
        console.log(error);
      }
    }
  } else if (event.type === "ANNOTATION_UPDATED") {
    annotationStore.updateAnnotation(event.data)
    // console.log(event)
  } else if (event.type === "ANNOTATION_DELETED") {
    annotationStore.deleteAnnotation(event.data)
  } else if (event.type === "CONTEXT_MENU_ITEM_CLICKED" && event.data === "verb-comment-highlight-note") {
    if (annotationSelected.value?.id) {
      const elm = document.getElementById(annotationSelected.value.id)?.querySelector(`textarea[class="v-field__input"]`)
      elm.focus()
    }
    // else {
    //     if (currentTab.value !== '1') {
    //         document.querySelector('button.v-tab[value="1"]')?.click()
    //         await nextTick()

    //         setTimeout(() => annotationStore.typeNewNote(dataPreviewSelectionEnd, `${infoFile.id}`, annotationManager), 290)
    //     } else {
    //         document.querySelector('div.content-text').scrollTop = 0
    //         annotationStore.typeNewNote(dataPreviewSelectionEnd, `${infoFile.id}`, annotationManager)
    //     }
    // }
  } else if (Object.keys(dataPagesViewInChange).length === 0 && event.type === "PAGES_IN_VIEW_CHANGE") {
    dataPagesViewInChange = event.data
    console.log(dataPagesViewInChange, dataPreviewPageViewScrolled)
    if (Object.keys(dataPreviewPageViewScrolled).length) {
      let numPages = event.data.endPage.pageNumber - event.data.startPage.pageNumber + 1,
        distanceFromPageToMargin = 66.225 * 2,//93.36 * 2,
        distanceBetweenPages = 15

      pdfPageHeight.value = (dataPreviewPageViewScrolled.clientHeight - (numPages - 1) * distanceBetweenPages) / (dataPagesViewInChange.startPage.fractionVisible + dataPagesViewInChange.endPage.fractionVisible + numPages - 2)
      pdfPageWidth.value = dataPreviewPageViewScrolled.scrollWidth - distanceFromPageToMargin * 2
    }
  } else if (Object.keys(dataPreviewPageViewScrolled).length === 0 && event.type === "PREVIEW_PAGE_VIEW_SCROLLED") {
    dataPreviewPageViewScrolled = event.data
    console.log(dataPreviewPageViewScrolled)
    if (Object.keys(dataPagesViewInChange).length) {
      let numPages = dataPagesViewInChange.endPage.pageNumber - dataPagesViewInChange.startPage.pageNumber + 1,
        distanceFromPageToMargin = 66.225 * 2, // zoom level: FIT_WIDTH
        distanceBetweenPages = 15

      pdfPageHeight.value = (event.data.clientHeight - (numPages - 1) * distanceBetweenPages) / (dataPagesViewInChange.startPage.fractionVisible + dataPagesViewInChange.endPage.fractionVisible + numPages - 2)
      pdfPageWidth.value = event.data.scrollWidth - distanceFromPageToMargin
    }

  } else if (event.type === "ANNOTATION_COUNT") {
    numAnnotations.value = event.data
  }
  // console.log(event)
}

async function formatAnnotations() {
  let tempt = {}
  for (let i = 1; i <= numPages.value; i++) {
    await annotationManager.getAnnotations({
      pageRange: { startPage: i, endPage: i }
    })
      .then((result) => {
        if (result.length) {
          tempt[i] = result
        }
      })
      .catch(error => console.log(error))
  }
  // console.log(annotationObj.value)
  annotationObj.value = { ...tempt }
}

function search() {
  if (!searchText.value)
    return
  viewerApis.search(searchText.value)
    .then((sObj) => {
      searchObject = sObj;
      searchObject.onResultsUpdate((result) => searchCallback(result))
        .then()
        .catch((error) => {
          console.log(error)
        });
    })
    .catch(() => {
      currentResultIndex.value = 0
      totalResults.value = 0
    })
}

function searchCallback(searchResult) {
  currentResultIndex.value = searchResult.currentResult.index;
  totalResults.value = searchResult.totalResults;
}

/* Select the next search result */
function nextSearchResult() {
  if (currentResultIndex.value >= totalResults.value)
    return
  searchObject.next()
    .then()
    .catch(function (error) {
      console.log(error);
    });
}

/* Select the previous search result */
function previousSearchResult() {
  if (currentResultIndex.value <= 0)
    return
  searchObject.previous()
    .then()
    .catch(function (error) {
      console.log(error);
    });
}

/* Cancel the search operation and clear the search results */
function clearSearchResult() {
  searchText.value = ""
  currentResultIndex.value = 0
  totalResults.value = 0
  if (searchObject) {
    searchObject.clear()
      .then()
      .catch(function (error) {
        console.log(error);
      });
  }
}

function nextPage() {
  if (currentPage.value >= numPages.value)
    return
  let temp = parseInt(currentPage.value + 1)
  viewerApis.gotoLocation(temp)
    .then() //result => console.log("nextPage: ",temp, result)
    .catch(error => console.log(error));
}

function previousPage() {
  if (currentPage.value <= 1)
    return
  let temp = parseInt(currentPage.value - 1)
  viewerApis.gotoLocation(temp)
    .then() //result => console.log("previousPage: ", temp, result)
    .catch(error => console.log(error));
}

function gotoPage() {
  if (currentPage.value < 1 || currentPage.value > numPages.value)
    return
  viewerApis.gotoLocation(parseInt(currentPage.value))
    .then() //result => console.log("gotoPage", result)
    .catch(error => console.log(error))
}

function download() {
  if (!contentPdfFile && !file) {
    alert("Đã có lỗi xảy ra. Vui lòng thử lại sau !")
    return
  }
  // console.log("contentPdfFile", contentPdfFile)
  // console.log("file", file)
  // Create a blob from the array buffer
  let blob = new Blob([contentPdfFile ? contentPdfFile : file], { type: "application/pdf" }); //contentPdfFile

  // Create an object URL for the blob
  let url = window.URL.createObjectURL(blob);

  // Create a link element
  let link = document.createElement('a');
  link.href = url;
  link.download = pdfTitle.value;

  // Append the link to the body
  document.body.appendChild(link);

  // Start the download
  link.click();

  // Clean up
  document.body.removeChild(link);
}

function print() {
  if (!contentPdfFile && !file) {
    alert("Đã có lỗi xảy ra. Vui lòng thử lại sau !")
    return
  }
  // console.log("contentPdfFile", contentPdfFile)
  // console.log("file", file)
  // Create a blob from the array buffer
  let blob = new Blob([contentPdfFile ? contentPdfFile : file], { type: "application/pdf" }); //contentPdfFile

  // Create an object URL for the blob
  let url = window.URL.createObjectURL(blob);

  let iframePre = document.getElementById("iframe-print")
  if (iframePre) iframePre.parentNode.removeChild(iframePre)

  // Write the Blob URL into the iframe
  let iframe = document.createElement('iframe');
  iframe.style.display = 'none';
  iframe.setAttribute('src', url)
  iframe.setAttribute('id', 'iframe-print')
  document.body.appendChild(iframe);

  // Print the iframe's content
  setTimeout(() => {
    iframe.contentWindow.print();
  }, 0)
}

function zoomOut() {
  viewerApis.getZoomAPIs().zoomOut()
    .then(result => console.log(result))
    .catch(error => console.log(error))
}

function zoomIn() {
  viewerApis.getZoomAPIs().zoomIn()
    .then()
    .catch(error => console.log(error))
}

onMounted(() => {
  addAdobeViewSdk()
})

watch(adobeApiPDFReady, (val) => {
  if (val) {
    let view = new window.AdobeDC.View({
      clientId: import.meta.env.VITE_ADOBE_CLIENT_ID,
      divId: "adobe-dc-view",
      sendAutoPDFAnalytics: false,
    });
    // console.log(view)
    adobeDCView = Object.freeze(view);
    console.log(adobeDCView)
    // openPdfFile()
    getFile()
  }
})

/* Translate Section */
const translationStore = useTranlationStore()
const { selectedText, showBanner, currentTab } = storeToRefs(translationStore)

const annotationStore = useAnnotationStore()
const { annotationObj, annotationSelected, pdfPageHeight, pdfPageWidth } = storeToRefs(annotationStore)

async function updateNumPageRead() {
  try {
    const res = await FileService.updateNumPageRead(infoFile.id, currentPage.value)
  } catch (error) {
    console.log(error)
  }
}

window.addEventListener('beforeunload', updateNumPageRead);

onBeforeUnmount(async () => {
  updateNumPageRead()
  window.removeEventListener('beforeunload', updateNumPageRead);
  useAnnotationStore().$reset()
})


</script>
