import createApiClient from "./api.service";
class Admin1Service {
  constructor(baseUrl = import.meta.env.VITE_ADMIN_1_SERVICE_API_ENDPOINT) {
    this.api = createApiClient(baseUrl);
  }

  async getAllAdmin2() {
    return await this.api.get(`/admin2-management/get-all-admin2`);
  }

  async createNewAdmin2(data) {
    return await this.api.post(`/admin2-management/create`, data);
  }

  async resetPassword(id) {
    return await this.api.put(`/admin2-management/reset-password/${id}`);
  }

  async deleteAdmin2(id) {
    return await this.api.delete(`/admin2-management/delete/${id}`);
  }
}
export default new Admin1Service();
