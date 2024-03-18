import createApiClient from "./api.service";
import { useUserStore } from '../store/user'
class AuthService {
    constructor(baseUrl = import.meta.env.VITE_AUTH_SERVICE_API_ENDPOINT) {
        this.api =  createApiClient(baseUrl);
    }

    async continueWithGoogle(data) {
        return (await this.api.post("/login", data)).data;
    }

    async loginStep1(data) {
        return (await this.api.post("/login/step1", data));
    }
    async loginStep2(data) {
        return (await this.api.post("/login/step2", data));
    }

    async signup(data) {
        return (await this.api.post("/signup", data));
    }

    async logout(){
        return (await this.api.put("/logout", {email: useUserStore().user.email},{
            headers: {
              Authorization: `Bearer ${useUserStore().user.token}`,
            },
          }));
    }


    // async verifyToken(data) {
    //     return (await this.api.post("/getinfo", data)).data;
    // }

    async forgotPasswordStep1(email) {
        return (await this.api.post("/forgot-password/step1", {email}));
    }
    async forgotPasswordStep2(token) {
        return (await this.api.get(`/forgot-password/step2?token=${token}`));
    }
    async forgotPasswordStep3(token, password) {
        return (await this.api.post(`/forgot-password/step3?token=${token}`,{password}));
    }

}
export default new AuthService();