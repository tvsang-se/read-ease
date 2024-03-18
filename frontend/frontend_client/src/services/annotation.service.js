import createApiClient from "./api.service";
import { useUserStore } from '../store/user'
class AnnotationService {
    constructor(baseUrl = import.meta.env.VITE_ANNOTATION_SERVICE_API_ENDPOINT) {
        this.api = createApiClient(baseUrl);
    }

    async addNewAnnotation(data) {
        return (await this.api.post(``,data,{
            headers: {
                // "Authorization": `Bearer ${useUserStore().user.token}`
            }
        }));
    }

    async updateAnnotation(data) {
        return (await this.api.put(``,data,{
            headers: {
                // "Authorization": `Bearer ${useUserStore().user.token}`
            }
        }));
    }

    async deleteAnnotation(annotationID) {
        return (await this.api.delete(`/${annotationID}`,{
            headers: {
                // "Authorization": `Bearer ${useUserStore().user.token}`
            }
        }));
    }

}
export default new AnnotationService();