import createApiClient from "./api.service";
import { useUserStore } from "../store/user";
class AuthService {
  constructor(baseUrl = import.meta.env.VITE_AUTH_SERVICE_API_ENDPOINT) {
    this.api = createApiClient(baseUrl);
  }

  async login(data) {
    return await this.api.post("/login", data);
  }

  async logout() {
    return await this.api.put(
      "/logout",
      { email: useUserStore().user.email },
      {
        headers: {
          Authorization: `Bearer ${useUserStore().user.token}`,
        },
      }
    );
  }
}
export default new AuthService();
