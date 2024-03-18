import { defineStore } from "pinia";
import { ref, watch } from "vue";
import { useRouter } from "vue-router";
import { useAlertStore } from "./alert";

export const useUserStore = defineStore(
  "user",
  () => {
    let user = ref({});

    function setInfoUser(info) {
      // if (info?.totalDocument === 0) info.documents = [];
      // if (!info?.collections) info.collections = [];
      // if (!info?.totalAccessTime) info.totalAccessTime = 0;
      // info.listYear = info.listYear.map(year => year[0])
      user.value = JSON.parse(
        JSON.stringify({
          ...info,
        })
      );
    }

    function reset(status = 200) {
      if (status === 407)
        alert(
          "Đã xảy khi lỗi trong quá trình xác thực. Vui lòng đăng nhập lại để tiếp tục sử dụng !"
        );
      else if (status === 409)
        alert(
          "Tài khoản đang được đăng nhập ở trình duyệt khác. Vui lòng thực hiện đăng nhập lại để tiếp tục sử dụng !"
        );

      user.value = {};
      window.localStorage.removeItem('user')
      setTimeout(() => window.location.reload(), 300);
    }
    return {
      user,
      setInfoUser,
      reset,
    };
  },
  {
    persist: true,
  }
);
