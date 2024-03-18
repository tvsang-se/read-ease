import axios from "axios";
import { useUserStore } from "../store/user";
class TranslateService {
    constructor(baseURL = import.meta.env.VITE_TRANSLATE_SERVER_API_ENDPOINT) {
        this.api = axios.create({baseURL})//createApiClient(baseUrl);
    }

    async translate(data) {
        return (await this.api.post("/api/translate", data, {
          headers: {
              "Authorization": `Bearer ${useUserStore().user.token}`
          }
      }));
    }

}
export default new TranslateService();
