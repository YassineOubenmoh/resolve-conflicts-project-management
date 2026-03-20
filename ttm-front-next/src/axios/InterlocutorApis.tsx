import axios from "axios";
import { ActionDto, Action, ReturnImpactDto } from "@/types/interlocutor";
import { RequiredActionDto } from "@/types/interlocutor";
import { setupAuthInterceptors } from "./authInterceptor";


export const interlocutorsApi = axios.create({
  baseURL: "http://localhost:8080",
});



// Access & refresh token configuration
setupAuthInterceptors(interlocutorsApi);




export const getGateProjectsByDepartmentAndProjectId = async (
  department: string,
  projectId: number
) => {
  const { data } = await interlocutorsApi.get(`departement-gateproject/gates-affected-project/${department}/${projectId}`);
  return Array.isArray(data) ? data : [];
};


export const getGateProjectsByDepartment = async (
  department: string,

) => {
  const { data } = await interlocutorsApi.get(`departement-gateproject/gates-affected-department/${department}`);
  return Array.isArray(data) ? data : [];
};



export const getGateProjectsByDepartmentSpoc = async (
  department: string,

) => {
  const { data } = await interlocutorsApi.get(`departement-gateproject/gates-affected-spoc/${department}`);
  return Array.isArray(data) ? data : [];
};


// ✅ Method to send ActionDto with document to /action/add

export const sendAction = async (
  actionDto: ActionDto,
  actionDocument: File
): Promise<ActionDto> => {
  const formData = new FormData();

  formData.append("actionDocument", actionDocument);

  // Append actionDto as a JSON string directly (not as Blob)
  formData.append("actionDto", JSON.stringify(actionDto));

  const { data } = await interlocutorsApi.post("/action/add", formData); // no need to set Content-Type manually

  return data;
};


export const sendFeedback = async (
  id: number,
  returnDto: ReturnImpactDto,
  responseDocument: File
): Promise<ActionDto> => {
  const formData = new FormData();

  // Correct name: must match the backend's MultipartFile name
  formData.append("responseDocument", responseDocument);

  // Append actionDto as a JSON string (correct name)
  formData.append("actionDto", JSON.stringify(returnDto));

  // PUT to /respond/{id}, not /respond/add
  const { data } = await interlocutorsApi.put(`action/respond/${id}`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });

  return data;
};









// Get Required Actions by Project ID
export const getRequiredActionsByProjectIdAndGate = async (
  projectId: number,
  gate: string
): Promise<RequiredActionDto[]> => {
  const { data } = await interlocutorsApi.get(
    `/required-action/gate-project-required-actions/${projectId}/${gate}`
  );
  return Array.isArray(data) ? data : [];
};



// Get Impact and Returns by Interlocutor
export const getImpactAndReturnsByInterlocutor = async (): Promise<Action[]> => {
  const { data } = await interlocutorsApi.get("/action/interlocutor-actions");
  return Array.isArray(data) ? data : [];
};


//Get Actions by Required Action ID
export const getRequiredActionById = async (
  requiredActionId: number
): Promise<RequiredActionDto> => {
  const { data } = await interlocutorsApi.get<RequiredActionDto>(
    `/required-action/find/${requiredActionId}`
  );

  // Assuming API returns the object directly, not an array.
  // If the API sometimes returns null/empty, you can throw or return a default value.

  if (!data) {
    throw new Error(`RequiredAction with id ${requiredActionId} not found`);
  }

  return data;
};




// ✅ Update Impact by ID (PUT multipart/form-data with file + JSON string)
export const updateImpact = async (
  id: number,
  actionDto: ActionDto,
  actionDocument: File
): Promise<ActionDto> => {
  const formData = new FormData();

  formData.append("actionDocument", actionDocument);
  formData.append("actionDto", JSON.stringify(actionDto));

  const { data } = await interlocutorsApi.put(`action/update-impact/${id}`, formData, {
    headers: {
      // Let axios set Content-Type to multipart/form-data with boundary automatically
      "Content-Type": "multipart/form-data",
    },
  });

  return data;
};



export const getRequiredActionByLabel = async (
  requiredActionLabel: string,
  projectId: number
): Promise<RequiredActionDto> => {
  try {
    const response = await interlocutorsApi.get<RequiredActionDto>(
      '/required-action/required-action-by-label',
      {
        params: {
          requiredActionLabel,
          projectId,
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error('Failed to fetch required action by label:', error);
    throw error;
  }
};






export const getActionsByRequiredAction = async (
  requiredActionId: number
): Promise<Action[]> => {
  try {
    const response = await interlocutorsApi.get<Action[]>(
      `/action/actions-by-required/${requiredActionId}`
    );
    return response.data;
  } catch (error) {
    console.error('Error fetching actions:', error);
    throw error;
  }
};


export const getActionById = async (
  actionId: number
): Promise<Action> => {
  const { data } = await interlocutorsApi.get<Action>(
    `/action/find/${actionId}`
  );

  if (!data) {
    throw new Error(`RequiredAction with id ${actionId} not found`);
  }

  return data;
};