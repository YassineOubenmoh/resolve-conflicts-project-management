

export interface BackendProject {
    id: number;
    ownerUsername: string;
    ownerFullName: string;
    title: string;
    description: string;
    marketType: string;
    projectType: string;
    ttmComitteeSubCategory: string;
    subcategoryCommercialCodir: string;
    isConfidential: boolean;
    dateStartTtm: string;
    expressionOfNeed: string;
    briefCommunication: string;
    briefCDG: string;
    regulatoryBrief: string;
    attachedDocuments: string[];
    comments: string[];
    dateCreation: string;
    moas: string[];
    trackingId: number;
    gateProjectIds: number[];
    currentGate: string;
    requiredActions: unknown; // Replace with actual type if known
    departments: string[];
  }
  
export interface BackendUser {
  firstName: string;
  lastName: string;
  username: string;
  department: string;
  roles: string[];
  email: string;
}

export interface InterlocutorEntry {
  id: number;
  interlocutor: string;
  project: string;
  role: string;
}


export interface InterlocutorDto {
  interlocutorSignalingId: number;
  interlocutorRespondingId: number;
  interlocutorSignalingFirstName: string;
  interlocutorRespondingFirstName: string;
  interlocutorSignalingLastName: string;
  interlocutorRespondingLastName: string;
  projectName: string;
  projectId: number;
}


export interface Notification {
  id: number;
  senderMail: string;
  receiverMail: string;
  content: string;
  consumed: boolean;
  createdAt: string; // Use string to represent ISO 8601 date-time
}

  

