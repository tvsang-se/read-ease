<template>
    <div class="c1">
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
                            <v-list-item disabled>
                                <v-list-item-title>
                                    {{ (pdfTitle.length > 35) ? (pdfTitle.slice(0, 33) + "...") : (pdfTitle) }}
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
                                prepend-inner-icon="mdi-magnify" single-line hide-details clearable
                                @click:prepend-inner="search" v-model="searchText" @click:clear="clearSearchResult"
                                @keyup.enter="search">

                                <template v-slot:append>
                                    <span v-show="searchObject?.previous">{{ this.currentResultIndex }} / {{
                                        this.totalResults }}</span>
                                    <v-icon size="medium" @click="previousSearchResult" class="ml-3">mdi-less-than</v-icon>
                                    <v-icon size="medium" @click="nextSearchResult"
                                        class="ml-2 mr-3">mdi-greater-than</v-icon>
                                </template>
                            </v-text-field>
                        </v-card>
                    </v-menu>

                    <v-tabs class="mt-2" v-model="tab" direction="vertical" color="#00ADEF" mandatory="false">
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
                                    <v-icon v-bind="props" color="#00ADEF" size="xx-large"
                                        icon="mdi-bookmark-multiple"></v-icon>
                                </template>
                                <span>Xem đánh dấu trang</span>
                            </v-tooltip>
                        </v-tab>

                        <v-tab value="3">
                            <v-tooltip location="left">
                                <template v-slot:activator="{ props }">
                                    <v-badge dot v-if="showBagesTranslateIcon" color="warning">
                                        <v-icon v-bind="props" color="#00ADEF" size="xx-large"
                                            icon="mdi-translate"></v-icon>
                                    </v-badge>
                                    <v-icon v-else v-bind="props" color="#00ADEF" size="xx-large"
                                        icon="mdi-translate"></v-icon>
                                </template>
                                <span>Dịch từ hoặc cụm từ</span>
                            </v-tooltip>
                        </v-tab>
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
            <div class="content" v-show="tab > -1">
                <v-card elevation="0" class="mt-2 pb-4" min-height="3.4rem">
                    <template v-slot:prepend>
                        <v-icon @click="tab = -1">mdi-close</v-icon>
                    </template>
                    <template v-slot:title>
                        {{ titleOfContentTab }}
                    </template>
                    <v-banner lines="one" color="info" v-if="showBanner">
                        <template v-slot:text>
                            Dịch đoạn văn vừa chọn
                        </template>

                        <template v-slot:actions>
                            <v-btn @click="translateSelectedText">
                                OK
                            </v-btn>

                            <v-btn @click="showBanner = false">
                                Huỷ
                            </v-btn>
                        </template>
                    </v-banner>
                </v-card>
                <div class="content-text">
                    <v-window v-model="tab" mandatory="false">
                        <v-window-item value="1">
                            <v-card flat>
                                <v-card-text>
                                    <p>
                                        Sed aliquam ultrices mauris. Donec posuere vulputate arcu.
                                        Morbi ac felis. Etiam feugiat lorem non metus. Sed a libero.
                                    </p>

                                    <p>
                                        Nam ipsum risus, rutrum vitae, vestibulum eu, molestie vel,
                                        lacus. Aenean tellus metus, bibendum sed, posuere ac, mattis
                                        non, nunc. Aliquam lobortis. Aliquam lobortis. Suspendisse non
                                        nisl sit amet velit hendrerit rutrum.
                                    </p>

                                    <p class="mb-0">
                                        Phasellus dolor. Fusce neque. Fusce fermentum odio nec arcu.
                                        Pellentesque libero tortor, tincidunt et, tincidunt eget,
                                        semper nec, quam. Phasellus blandit leo ut odio.
                                    </p>
                                </v-card-text>
                            </v-card>
                        </v-window-item>
                        <v-window-item value="2" v-show="bookmarks.length">
                            <v-card flat>
                                <v-card-text>
                                    <TableOfContent :bookmarks="bookmarks" :viewerApis="viewerApis" :currentIndex="0"
                                        v-if="bookmarks && viewerApis"></TableOfContent>
                                </v-card-text>
                            </v-card>
                        </v-window-item>
                        <v-window-item value="3">
                            <v-card class="mx-auto" max-width="344">
                                <v-card-title class="text-body-1 ma-0 pa-0 mx-4 d-flex justify-space-between align-center">
                                    Nội dung cần dịch: {{ currentLanguage }}
                                    <v-icon color="#00ADEF" @click="speakText(urlAudios, 0)"
                                        v-show="urlAudios.length && !isSpeakingText">mdi-volume-high</v-icon>
                                    <v-icon color="#00ADEF" @click="pauseAudioText"
                                        v-show="urlAudios.length && isSpeakingText">mdi-square-rounded</v-icon>
                                    <!-- <v-btn @click="speakText(0)" v-show="urlAudios.length">Speak</v-btn> -->
                                </v-card-title>
                                <v-card-item class="py-0">
                                    <v-textarea hint hide-details maxlength="6000" variant="outlined"
                                        v-model="needTranslatedText"
                                        :rows="(needTranslatedText.length > 250) ? '9' : '6'"></v-textarea>
                                    <v-btn variant="text" density="compact" @click="translate"
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
                                    <v-textarea hide-details hint variant="outlined"
                                        :rows="(needTranslatedText.length > 250) ? '9' : '6'" v-model="translatedText"
                                        readonly></v-textarea>
                                </v-card-item>
                            </v-card>
                        </v-window-item>
                        <v-overlay contained v-model="loadingTranslate" class="align-center justify-center">
                            <v-progress-circular color="#00ADEF" indeterminate size="64"></v-progress-circular>
                        </v-overlay>
                    </v-window>
                </div>


            </div>
        </div>
    </div>
</template>
  
<style>
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
    width: 100vw;
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    /* padding: 15px; */
    padding-left: 48px;
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
</style>

<script>
import TranslateService from '../services/translate.service';
import { languagesObj, languagesArr } from '../etc/languages'
import { annotations } from '../etc/annotationList'
import TableOfContent from '../components/TableOfContent.vue';
export default {
    name: "ReadFile",
    mixins: [],
    components: {
        TableOfContent
    },
    mounted() {
        console.log("mounted", this.adobeDCView)
        if (this.adobeDCView) {
            this.openPDF()
        }
        else {
            let el = document.getElementById('src-adobe-view-sdk')
            if (el) {
                window.location.reload()
            }
            document.addEventListener("adobe_dc_view_sdk.ready", () => {
                this.adobeApiPDFReady = true;
                console.log("Adobe created with adobe_dc_view_sdk.ready");
            });
            // Dynamically load Adobe SDK Viewer for this page
            const plugin = document.createElement("script");
            plugin.setAttribute('id', 'src-adobe-view-sdk')
            plugin.setAttribute("src", "https://acrobatservices.adobe.com/view-sdk/viewer.js");
            plugin.async = true;
            document.head.appendChild(plugin);
        }
    },
    data() {
        const langsObj = languagesObj;
        const langsArr = languagesArr;
        const targetLanguageCode = "vi";
        const targetLanguage = Object.keys(langsObj).find(key => langsObj[key] === targetLanguageCode);
        return {
            adobeApiPDFReady: false,
            adobeDCView: null,
            previewFilePromise: null,
            selectedText: "",
            translatedText: "",
            needTranslatedText: "",
            urlAudios: [],
            langsObj,
            langsArr,
            targetLanguage,
            currentLanguageCode: "",
            currentPage: 1,
            tab: -1,
            searchText: "",
            searchObject: null,
            currentResultIndex: 0,
            totalResults: 0,
            viewerApis: {},
            bookmarks: [],
            currentPageZoom: -1,
            pdfTitle: "",
            numPages: 0,
            rules: {
                valid: value => (value >= 1 && (this.numPages === 0 ? (value >= 1) : (value <= this.numPages))) || "",
            },
            showBanner: false,
            loadingTranslate: false,
            isSpeakingText: false,
            contentPdfFile: null,
        };
    },
    computed: {
        targetLanguageCode() {
            return this.langsObj[this.targetLanguage];
        },
        currentLanguage() {
            if (this.currentLanguageCode) {
                const temp = Object.keys(this.langsObj).find(key => this.langsObj[key] === this.currentLanguageCode);
                return `[${temp.split(' ')[0]}]`
            }
            else return ""
        },
        titleOfContentTab() {
            const obj = ["Quản lý chú thích", "Mục lục", "Dịch ngôn ngữ"];
            return obj[this.tab - 1];
        },
        showBagesTranslateIcon() {
            // console.log(this.selectedText, this.tab)
            return (this.selectedText !== '' && this.tab !== "3" && !this.translatedText) ? true : false
        },
    },
    methods: {
        openPDF() {
            const viewerConfig = {
                enableAnnotationAPIs: true,
                includePDFAnnotations: true,
                enableLinearization: true,
                defaultViewMode: "FIT_WIDTH",
                enableSearchAPIs: true,
            }
            console.log("Trying to open PDF");
            // Opening preview with default settings from https://developer.adobe.com/document-services/docs/overview/pdf-embed-api/#live-demo
            this.previewFilePromise = this.adobeDCView.previewFile({
                content: {
                    location: {
                        url:
                            "https://documentcloud.adobe.com/view-sdk-demo/PDFs/Bodea Brochure.pdf",
                        // "./You Don't Know JS_ Scope & Closures.pdf"
                    },
                },
                metaData: { fileName: "Bodea Brochure.pdf", id: "6d07d124-ac85-43b3-a867-36930f502ac6" },
            }, viewerConfig);
            const profile = {
                userProfile: {
                    name: 'You',
                }
            };
            this.adobeDCView.registerCallback(AdobeDC.View.Enum.CallbackType.GET_USER_PROFILE_API, function () {
                return new Promise((resolve, reject) => {
                    resolve({
                        code: AdobeDC.View.Enum.ApiResponseCode.SUCCESS,
                        data: profile
                    });
                });
            });
            const saveOptions = {
                autoSaveFrequency: 1,
                enableFocusPolling: false,
                showSaveButton: false
            };
            this.adobeDCView.registerCallback(AdobeDC.View.Enum.CallbackType.SAVE_API, (metaData, content, options) => {
                this.contentPdfFile = content
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
            this.previewFilePromise.then((adobeViewer) => {
                adobeViewer.getAPIs().then(apis => {
                    this.viewerApis = apis
                    apis.getBookmarkAPIs().getBookmarks()
                        .then(result => {
                            console.log(result)
                            if (result.length > 0)
                                this.bookmarks = result
                            else this.bookmarks = []
                        })
                        .catch(error => console.log(error));
                });
                adobeViewer.getAPIs().then(apis => {
                    apis.gotoLocation(3, 20, 40)
                        .then()
                        .catch(error => console.log(error));
                });
                adobeViewer.getAPIs().then(apis => {
                    apis.getCurrentPage()
                        .then(result => this.currentPage = result)
                        .catch(error => console.log(error));
                });

                adobeViewer.getAnnotationManager().then(function (annotationManager) {
                    /* API to add annotations */
                    annotationManager.addAnnotations(annotations)
                        .then(function () {
                            console.log("Annotations added through API successfully")
                        })
                        .catch(function (error) {
                            console.log(error)
                        });

                    /* API to register events listener */
                    annotationManager.registerEventListener(
                        function (event) {
                            console.log(event)
                        },
                        {
                            /* Pass the list of events in listenOn. */
                            /* If no event is passed in listenOn, then all the annotation events will be received. */
                            listenOn: [
                                /* "ANNOTATION_ADDED", "ANNOTATION_CLICKED" */
                            ]
                        }
                    );
                });

                adobeViewer.getAnnotationManager().then(annotationManager => {
                    let resultObj = {}
                    setTimeout(() => {
                        console.time('executionFunctionToBeMeasured');
                        for (let i = 1; i <= 7; i++) {
                            annotationManager.getAnnotations({
                                pageRange: { startPage: i, endPage: i }
                            })
                                .then(result => {
                                    if (result.length)
                                        resultObj[i] = result
                                    // console.log("Page ", i, ":", result)
                                })
                                .catch(error => console.log(error));
                        }
                        console.timeEnd('executionFunctionToBeMeasured');
                        console.log(resultObj)
                    }, 5000)
                });


                adobeViewer.getAPIs().then(apis => {
                    apis.getPDFMetadata()
                        .then(result => {
                            this.pdfTitle = result.pdfTitle
                            this.numPages = result.numPages
                        })
                        .catch(error => console.log(error));
                });
                this.adobeDCView.registerCallback(AdobeDC.View.Enum.CallbackType.EVENT_LISTENER, (event) => this.func(event), {
                    listenOn: [AdobeDC.View.Enum.FilePreviewEvents.PREVIEW_SELECTION_END, AdobeDC.View.Enum.FilePreviewEvents.CURRENT_ACTIVE_PAGE, AdobeDC.View.Enum.PDFAnalyticsEvents.PREVIEW_PAGE_CLICK,
                    AdobeDC.View.Enum.PDFAnalyticsEvents.TEXT_COPY],
                    enableFilePreviewEvents: true,
                    enablePDFAnalytics: true
                });
            })
        },
        zoomOut() {
            this.viewerApis.getZoomAPIs().zoomOut()
                .then(function (result) {
                    // console.log("Zoom out: ", result)
                })
                .catch(error => console.log(error))
        },
        zoomIn() {
            this.viewerApis.getZoomAPIs().zoomIn()
                .then(function (result) {
                    // console.log("Zoom in: ", result)
                })
                .catch(error => console.log(error))
        },
        func(event) {
            if (event.type === "PREVIEW_SELECTION_END") {
                this.previewFilePromise.then(adobeViewer => {
                    adobeViewer.getAPIs().then(apis => {
                        setTimeout(() => apis.getSelectedContent()
                            .then(result => {
                                this.selectedText = result.data.replace(/\s+/g, ' ').trim();
                            })
                            .catch(error => console.log(error)), 10);
                    });
                });
            } else if (event.type === "CURRENT_ACTIVE_PAGE") {
                this.currentPage = event.data.pageNumber
            } else if (event.type === "PREVIEW_PAGE_CLICK") {
                this.searchText = ""
            }
        },
        async translate() {
            if (this.needTranslatedText.length > 6000) {
                alert("Vượt quá số lượng ký tự cho phép !");
                return;
            }
            try {
                this.loadingTranslate = true
                const res = await TranslateService.translate({ q: this.needTranslatedText, targetLanguage: this.targetLanguageCode });
                if (res.status / 100 === 2) {
                    const { text, lang, urlAudios } = res.data;
                    this.translatedText = text;
                    this.urlAudios = urlAudios;
                    this.currentLanguageCode = lang
                }
            }
            catch (error) {
                alert("Đã xảy ra lỗi. Vui lòng thử lại sau !");
                console.log(error);
            } finally {
                this.loadingTranslate = false
            }
        },
        speakText(urlAudios, id) {
            this.isSpeakingText = true
            this.audio = new Audio(urlAudios[id]);

            this.audio.onended = () => {
                if (id < urlAudios.length - 1) {
                    this.speakText(urlAudios, id + 1);
                } else {
                    this.isSpeakingText = false
                }
            };
            this.audio.play();
        },
        pauseAudioText() {
            if (this.audio) {
                this.audio.pause();
                this.isSpeakingText = false
            }
        },
        search() {
            if (!this.searchText)
                return
            this.viewerApis.search(this.searchText)
                .then((sObj) => {
                    this.searchObject = sObj;
                    this.searchObject.onResultsUpdate((result) => this.searchCallback(result))
                        .then()
                        .catch((error) => {
                            console.log(error)
                        });
                })
                .catch((error) => {
                    this.currentResultIndex = 0
                    this.totalResults = 0
                })
        },

        searchCallback(searchResult) {
            // console.log(searchResult)
            this.currentResultIndex = searchResult.currentResult.index;
            this.totalResults = searchResult.totalResults;
        },

        /* Select the next search result */
        nextSearchResult() {
            if (this.currentResultIndex >= this.totalResults) return
            this.searchObject.next()
                .then(function () {
                    // console.log("Navigate to the next search result.");
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        /* Select the previous search result */
        previousSearchResult() {
            if (this.currentResultIndex <= 0) return
            this.searchObject.previous()
                .then(function () {
                    // console.log("Navigate to the previous search result.");
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        /* Cancel the search operation and clear the search results */
        clearSearchResult() {
            this.searchText = ""
            if (this.searchObject) {
                this.searchObject.clear()
                    .then()
                    .catch(function (error) {
                        console.log(error);
                    });
            }
        },
        nextPage() {
            if (this.currentPage >= this.numPages)
                return
            let temp = parseInt(this.currentPage + 1)
            this.viewerApis.gotoLocation(temp)
                .then() //result => console.log("nextPage: ",temp, result)
                .catch(error => console.log(error));
        },
        previousPage() {
            // console.log("previousPage")
            if (this.currentPage <= 1)
                return
            let temp = parseInt(this.currentPage - 1)
            this.viewerApis.gotoLocation(temp)
                .then() //result => console.log("previousPage: ", temp, result)
                .catch(error => console.log(error));
        },
        gotoPage() {
            if (this.currentPage < 1 || this.currentPage > this.numPages)
                return
            this.viewerApis.gotoLocation(parseInt(this.currentPage))
                .then() //result => console.log("gotoPage", result)
                .catch(error => console.log(error));
        },
        async translateSelectedText() {
            if (this.selectedText.length > 0) {
                this.needTranslatedText = this.selectedText
                this.translatedText = ""
                this.urlAudios = ""
                await this.translate()
            }
            this.showBanner = false
        },
        download() {
            if (!this.contentPdfFile) {
                alert("Đã có lỗi xảy ra. Vui lòng thử lại sau !")
                return
            }
            // Create a blob from the array buffer
            let blob = new Blob([this.contentPdfFile], { type: "application/pdf" });

            // Create an object URL for the blob
            let url = window.URL.createObjectURL(blob);

            // Create a link element
            let link = document.createElement('a');
            link.href = url;
            link.download = this.pdfTitle;

            // Append the link to the body
            document.body.appendChild(link);

            // Start the download
            link.click();

            // Clean up
            document.body.removeChild(link);
        },
        print() {
            if (!this.contentPdfFile) {
                alert("Đã có lỗi xảy ra. Vui lòng thử lại sau !")
                return
            }
            // Create a blob from the array buffer
            let blob = new Blob([this.contentPdfFile], { type: "application/pdf" });

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
        },
    },
    watch: {
        adobeApiPDFReady(val) {
            if (val) {
                // val == true ; Adobe is loaded on page
                let view = new window.AdobeDC.View({
                    clientId: import.meta.env.VITE_ADOBE_CLIENT_ID,
                    divId: "adobe-dc-view",
                    sendAutoPDFAnalytics: false,
                });
                this.adobeDCView = Object.freeze(view);
                this.openPDF();
            }
            // console.log("Adobe is mounted with Client ID");
        },
        selectedText(val) {
            if (this.tab !== "3") {
                this.needTranslatedText = this.selectedText
                this.translatedText = ""
                this.urlAudios = ""
                this.pauseAudioText()
            } else {
                this.showBanner = true
            }
        },
        tab(val) {
            if (!this.showBagesTranslateIcon && val === "3" && this.selectedText && !this.translatedText)
                this.translate()
        },
        needTranslatedText(val) {
            this.translatedText = ""
            this.urlAudios = ""
            this.currentLanguageCode = ""
            this.pauseAudioText()
        },
        targetLanguage(newVal, oldVal) {
            if (newVal !== oldVal) {
                this.translatedText = ""
                this.urlAudios = ""
                this.pauseAudioText()
            }
        }
    },
};
</script>