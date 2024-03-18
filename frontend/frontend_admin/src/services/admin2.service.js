import createApiClient from "./api.service";
class Admin2Service {
  constructor(baseUrl = import.meta.env.VITE_ADMIN_2_SERVICE_API_ENDPOINT) {
    this.api = createApiClient(baseUrl);
  }

  async getDataHome() {
    return await this.api.get(`/user-management/getdatahome`);
  }

  async getStatisticCreatedUsers(time) {
    return await this.api.get(`/user-management/count-user-created/?time=${time}`);
  }

  async getStatisticCreatedUsersByYear(time,year) {
    return await this.api.get(`/user-management/count-user-created-by-year/?time=${time}&year=${year}`);
  }


  async changePassword(data) {
    return await this.api.put(`/account-management/change-password`, data);
  }
}
export default new Admin2Service();
