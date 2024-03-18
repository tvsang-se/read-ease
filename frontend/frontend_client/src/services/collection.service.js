import createApiClient from "./api.service";
import { useUserStore } from "../store/user";
class CollectionService {
  constructor(baseUrl = import.meta.env.VITE_COLLECTION_SERVICE_API_ENDPOINT) {
    this.api = createApiClient(baseUrl);
  }

  async getCollections() {
    return await this.api.get(`/get-all`, {
      headers: {
        // Authorization: `Bearer ${useUserStore().user.token}`,
      },
    });
  }

  async createNewCollection(data) {
    return await this.api.post(`/`, data, {
      headers: {
        // Authorization: `Bearer ${useUserStore().user.token}`,
      },
    });
  }

  async addDocument(collectionID, docID) {
    return await this.api.put(
      `${collectionID}/add-document/${docID}`,
      {},
      {
        headers: {
          // Authorization: `Bearer ${useUserStore().user.token}`,
        },
      }
    );
  }

  async addDocuments(collectionID, docIDs) {
    return await this.api.put(
      `${collectionID}/add-document/`,
      { docIDs },
      {
        headers: {
          // Authorization: `Bearer ${useUserStore().user.token}`,
        },
      }
    );
  }

  async removeDocument(collectionID, docID) {
    return await this.api.delete(`${collectionID}/remove-document/${docID}`, {
      headers: {
        // Authorization: `Bearer ${useUserStore().user.token}`,
      },
    });
  }

  async removeDocuments(collectionID, docIDs) {
    return await this.api.put(
      `${collectionID}/remove-document/`,
      { docIDs },
      {
        headers: {
          // Authorization: `Bearer ${useUserStore().user.token}`,
        },
      }
    );
  }

  async renameCollection(collectionID, data) {
    return await this.api.put(`/${collectionID}`, data, {
      headers: {
        // Authorization: `Bearer ${useUserStore().user.token}`,
      },
    });
  }

  async deleteCollection(collectionID) {
    return await this.api.delete(`/${collectionID}`, {
      headers: {
        // Authorization: `Bearer ${useUserStore().user.token}`,
      },
    });
  }
}
export default new CollectionService();
