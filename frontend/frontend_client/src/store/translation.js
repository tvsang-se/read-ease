import { defineStore } from "pinia";
import { ref, watch, computed } from "vue";
import { languagesObj } from "../etc/languages";
import TranslateService from "../services/translate.service";
import { useUserStore } from './user'

export const useTranlationStore = defineStore("tranlation", () => {
  const langsObj = languagesObj;

  const targetLanguageCodeDefault = (useUserStore().user?.targetLanguage)?useUserStore().user.targetLanguage:"vi";
  const targetLanguageDefault = Object.keys(langsObj).find(
    (key) => langsObj[key] === targetLanguageCodeDefault
  );
  const targetLanguage = ref(targetLanguageDefault);
  const targetLanguageCode = computed(() => {
    return langsObj[targetLanguage.value];
  });

  const currentLanguageCode = ref("");
  const currentLanguage = computed(() => {
    if (currentLanguageCode.value) {
      const tempt = Object.keys(langsObj).find(
        (key) => langsObj[key] === currentLanguageCode.value
      );
      return `[${tempt.split(" ")[0]}]`;
    } else return "";
  });

  const loading = ref(false);

  let selectedText = ref(""),
    currentTab = ref(-1),
    needTranslatedText = ref(""),
    translatedText = ref(""),
    urlAudios = ref([]),
    audio = ref(null),
    isSpeakingText = ref(false),
    showBanner = ref(false);

  function pauseAudioText() {
    if (audio.value) {
      audio.value.pause();
      isSpeakingText.value = false;
    }
  }

  watch(selectedText, (val) => {
    if(val.length === 0 || val === " ")
      return
    if (currentTab.value !== "3") {
      needTranslatedText.value = selectedText.value;
      translatedText.value = "";
      urlAudios.value = "";
      pauseAudioText();
    } else {
      if(currentTab.value === "3")
        showBanner.value = true;
    }
  });

  watch(currentTab, (val) => {
    if (val === "3" && selectedText.value && !translatedText.value) translate();
  });

  watch(targetLanguage, () => {
    translatedText.value = "";
    urlAudios.value = "";
    pauseAudioText();
  });

  async function translate() {
    if (needTranslatedText.value.length > 6000) {
      alert("Vượt quá số lượng ký tự cho phép !");
      return;
    }
    try {
      loading.value = true;
      const res = await TranslateService.translate({
        q: needTranslatedText.value,
        targetLanguage: targetLanguageCode.value,
      });
      if (res.status / 100 === 2) {
        const { text, lang, urlAudios: urlAudioss } = res.data;
        translatedText.value = text;
        urlAudios.value = urlAudioss;
        currentLanguageCode.value = lang;
      }
    } catch (error) {
      alert("Đã xảy ra lỗi. Vui lòng thử lại sau !");
      console.log(error);
    } finally {
      loading.value = false;
    }
  }

  function speakText(idx) {
    isSpeakingText.value = true;
    audio.value = new Audio(urlAudios.value[idx]);

    audio.value.onended = () => {
      if (idx < urlAudios.value.length - 1) {
        speakText(idx + 1);
      } else {
        isSpeakingText.value = false;
      }
    };
    audio.value.play();
  }

  async function translateNewSelectedText() {
    if (selectedText.value.length > 0) {
      needTranslatedText.value = selectedText.value;
      showBanner.value = false
      pauseAudioText()
      await translate();
    }
  }

  const showBagesTranslateIcon = computed(() => {
    return (
      currentTab.value != "3" && selectedText.value && !translatedText.value
    );
  });

  function updateTargetLanguage(newTargetLang) {
    targetLanguage.value = newTargetLang
  }

  watch(needTranslatedText, () => {
    translatedText.value = ""
    urlAudios.value = ""
    currentLanguageCode.value = ""
    pauseAudioText()
  })
  return {
    currentTab,
    showBanner,
    selectedText,
    needTranslatedText,
    targetLanguage,
    currentLanguage,
    translatedText,
    urlAudios,
    audio,
    loading,
    isSpeakingText,
    showBagesTranslateIcon,
    pauseAudioText,
    translate,
    speakText,
    translateNewSelectedText,
    updateTargetLanguage
  };
});
