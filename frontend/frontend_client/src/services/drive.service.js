import axios from "axios";
import createApiClient from "./api.service";
import { useUserStore } from "../store/user";
class DriveService {
  constructor() {
    //baseUrl = import.meta.env.VITE_DRIVE_SERVICE_API_ENDPOINT
    this.api = axios.create()  //createApiClient(); //baseUrl
  }

  async uploadFileToGGDrive(formData, token) {
    return await this.api.post(
      `https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart`,
      formData,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
  }

  async setPermissionsOfFileInGGDrive(fileID, token) {
    return await this.api.post(
      `https://www.googleapis.com/drive/v2/files/${fileID}/permissions`,
      { type: "anyone", value: "anyone", role: "reader" },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
  }

  async getFileFromGGDrive(url, token) {
    return await this.api.get(url, {
      //export?mimeType=pdf
      headers: {
        Authorization: `Bearer ${token}`,
      },
      responseType: "arraybuffer",
      dataType: "blob",
    });
  }

  async deleteFileOnGGDrive(fileID, token) {
    return await this.api.delete(`https://www.googleapis.com/drive/v3/files/${fileID}`, {
      //export?mimeType=pdf
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  }
}
export default new DriveService();
