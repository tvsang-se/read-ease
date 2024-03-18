import createApiClient from "./api.service";
import { useUserStore } from "../store/user";
class FileService {
  constructor(baseUrl = import.meta.env.VITE_FILE_SERVICE_API_ENDPOINT) {
    this.api = createApiClient(baseUrl);
  }

  async getGoogleAccessToken() {
    return await this.api.get(`document/required-upload`, {
      headers: {
        // Authorization: `Bearer ${useUserStore().user.token}`,
      },
    });
  }

  async addNewDocument(data) {
    return await this.api.post(`document/`, data, {
      headers: {
        // Authorization: `Bearer ${useUserStore().user.token}`,
      },
    });
  }

  async addNewOfficeDocument(type,name,idDriveFolder,data) {
    const params = new URLSearchParams({
      type,
      name,
      idDriveFolder
    })
    let contentType = {
      "doc": "application/msword",
      "docx": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
      "xls": "application/vnd.ms-excel",
      "xlsx": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      "ppt": "application/vnd.ms-powerpoint",
      "pptx": "application/vnd.openxmlformats-officedocument.presentationml.presentation"
    }
    return await this.api.post(`document/convert-to-pdf?${params}`, data, {
      headers: {
        // Authorization: `Bearer ${useUserStore().user.token}`,
        "Accept-Encoding": "gzip, deflate, br",
        "Content-Type": contentType[type]
      },
    });
  }

  async renameFile(data) {
    return await this.api.put(`document/rename`, data, {
      headers: {
        // Authorization: `Bearer ${useUserStore().user.token}`,
      },
    });
  }

  async getFiles(searchParams) {
    return await this.api.get(`document?${searchParams}`, {
      headers: {
        // Authorization: `Bearer ${useUserStore().user.token}`,
      },
    });
  }

  async getFile(id) {
    return await this.api.get(`document/${id}`, {
      headers: {
        // Authorization: `Bearer ${useUserStore().user.token}`,
      },
    });
  }

  async checkDuplicateName(searchParams) {
    return await this.api.get(`document/check-duplicate-name?${searchParams}`, {
      headers: {
        // Authorization: `Bearer ${useUserStore().user.token}`,
      },
    });
  }

  async deleteFile(fileID) {
    return await this.api.delete(`document/delete/${fileID}`, {
      headers: {
        // Authorization: `Bearer ${useUserStore().user.token}`,
      },
    });
  }

  async ratingFile({ ID, star }) {
    return await this.api.put(
      `document/rating/${ID}`,
      { star },
      {
        headers: {
          // Authorization: `Bearer ${useUserStore().user.token}`,
        },
      }
    );
  }

  async updateNumPageRead(ID, numberOfPagesReading) {
    return await this.api.put(
      `document/update/${ID}`,
      { numberOfPagesReading },
      {
        headers: {
          // Authorization: `Bearer ${useUserStore().user.token}`,
        },
      }
    );
  }

  async getCollections(docID) {
    return await this.api.get(
      `document/${docID}/get-collection`,
      {
        headers: {
          // Authorization: `Bearer ${useUserStore().user.token}`,
        },
      }
    );
  }

  async addIntoListCollection(docID, colIDs) {
    return await this.api.put(
      `document/${docID}/add-into-list-collection/`,
      { colIDs },
      {
        headers: {
          // Authorization: `Bearer ${useUserStore().user.token}`,
        },
      }
    );
  }

  async removeOutListCollection(docID, colIDs) {
    return await this.api.put(
      `document/${docID}/remove-out-list-collection/`,
      { colIDs },
      {
        headers: {
          // Authorization: `Bearer ${useUserStore().user.token}`,
        },
      }
    );
  }
}
export default new FileService();
