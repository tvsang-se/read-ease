import { defineStore } from "pinia";
import { ref } from "vue";

export const useAuthStore = defineStore(
  "auth",
  () => {
    const email = ref("tranvansang6922@gmail.com"); //tranvansang6922@gmail.com
    const loading = ref(false);
    const inStep1 = ref(true)

    // watch(email, () => {
    //   if(email.value.length === 0)
    //     inStep1.value = true
    //   else
    //     inStep1.value = false
    // })

    // function setInStep1(bool){
    //   inStep1.value = bool
    // }

    function reset() {
      email.value = ""
      loading.value = false
      inStep1.value = true
    }

    return {
      email,
      loading,
      inStep1,
      reset
      // setInStep1
    };
  },
  {
    persist: true,
  }
);
