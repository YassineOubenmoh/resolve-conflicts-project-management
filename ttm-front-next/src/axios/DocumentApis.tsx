import { DocumentDto } from "@/types/document";
import axios from "axios";
import { setupAuthInterceptors } from "./authInterceptor";

export const documentApi = axios.create({
  baseURL: "http://localhost:8081/files",
});



// Access & refresh token configuration
setupAuthInterceptors(documentApi);





export const getProjects = async () => {
  const { data } = await documentApi.get("/all");
  return Array.isArray(data) ? data : []; // Ensure the response is an array
};

export const addProject = (project: unknown) => {
  return documentApi.post(`/add`, project);
};

export const getDocumentSizeByName = (docName: string) => {
  return documentApi.get(`/size/${docName}`);
};

export const getActionsDocumentsByProjectId = (projectId: number) => {
  return documentApi.get(`/actions-project/${projectId}`);
};




export const getDocumentsByAuthorUsername = async (
  authorName: string
): Promise<DocumentDto[]> => {
  try {
    const { data } = await documentApi.get(`/documents-by-author/${authorName}`);
    return Array.isArray(data) ? data : [];
  } catch (error) {
    console.error("Error fetching documents by author username:", error);
    return [];
  }
};




export const filterDocuments = (
  department?: string,
  authorName?: string,
  gateLabel?: string
) => {
  const params: Record<string, string> = {};

  if (department) {
    params.department = department;
  }

  if (authorName) {
    params.authorName = authorName;
  }

  if (gateLabel) {
    params.gateLabel = gateLabel;
  }

  return documentApi.get("/filter", { params });
};




export const downloadFile = async (fileName: string) => {
  try {
    const response = await documentApi.get(`/download/${fileName}`, {
      responseType: 'blob',
    });

    const blob = new Blob([response.data]);
    const downloadUrl = window.URL.createObjectURL(blob);

    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = fileName;
    document.body.appendChild(link);
    link.click();

    link.remove();
    window.URL.revokeObjectURL(downloadUrl);
  } catch (error) {
    console.error("Error downloading the file:", error);
  }
};
