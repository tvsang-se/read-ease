import { defineStore } from "pinia";
import { ref } from "vue";

export const useAlertStore = defineStore("alert", () => {
  const typeAlert = ref("");
  const titleAlert = ref("");
  const contentAlert = ref("");
  const timeAlert = ref(0);

  function setAlert(type, title, content, time) {
    
    typeAlert.value = type;
    titleAlert.value = title;
    contentAlert.value = content;
    timeAlert.value = time;
    setTimeout(() => timeAlert.value = 0, timeAlert.value)
  }

  function showCommonError(title) {
    typeAlert.value = "error";
    titleAlert.value = title;
    contentAlert.value = "Đã có lỗi xảy ra. Vui lòng thử lại sau !";
    timeAlert.value = 10000;
    setTimeout(() => timeAlert.value = 0, timeAlert.value)
  }

  // showCommonError("")

  function resetAlert() {
    typeAlert.value = "";
    titleAlert.value = "";
    contentAlert.value = "";
    timeAlert.value = 0;
  }

  return {
    typeAlert,
    titleAlert,
    contentAlert,
    timeAlert,
    setAlert,
    showCommonError,
    resetAlert
  };
});
