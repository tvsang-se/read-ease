import createApiClient from "./api.service";
import { useUserStore } from '../store/user'
class UserService {
    constructor(baseUrl = import.meta.env.VITE_USER_SERVICE_API_ENDPOINT) {
        this.api = createApiClient(baseUrl);
    }

    async getInfoUser(){
        return (await this.api.get(`${useUserStore().user.userID}`,{
            headers: {
                // "Authorization": `Bearer ${useUserStore().user.token}`
            }
        }));
    }

    async changePassword(data){
        return (await this.api.put(`/change-password`,data, {
            headers: {
                // "Authorization": `Bearer ${useUserStore().user.token}`
            }
        }));
    }

    async updateInfoUser(data){
        return (await this.api.put(`/update-information`,data, {
            headers: {
                // "Authorization": `Bearer ${useUserStore().user.token}`
            }
        }));
    }

}
export default new UserService();