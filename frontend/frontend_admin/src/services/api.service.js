import axios from "axios";
import { useUserStore } from "../store/user";
import { useRouter } from "vue-router";
import { useAlertStore } from "@/store/alert";
const commonConfig = {
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
    Authorization: `Bearer ${useUserStore().user.token}`,
  },
};

const configAuthAPI = {
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
};

export default (baseURL) => {
  let axiosInstance;
  let isAuthAPI = baseURL.includes("api/auth");
  // console.log(baseURL, isAuthAPI);
  if (isAuthAPI) axiosInstance = axios.create({ baseURL, configAuthAPI });
  else {
    axiosInstance = axios.create({
      baseURL,
      headers: { Authorization: `Bearer ${useUserStore().user.token}` },
    });
  }
  // console.log(useUserStore().user.token);
  // Add a response interceptor
  axiosInstance.interceptors.response.use(
    function (response) {
      return response;
    },
    function (error) {
      if (isAuthAPI) return Promise.reject(error);

      let status = error.response.status;
      if (status === 407) {
        useUserStore().reset(status);
      } else if (status === 409) {
        useUserStore().reset(status);
      } else {
        return Promise.reject(error);
      }
    }
  );
  return axiosInstance;
};
