

export interface GateProjectFront {
  projectId: number;
  titleProject: string;
  gate: string;
  currentGate: boolean;
  requiredActions: string[];
}


export interface RequiredActionDto {
    id: number;
    requiredAction: string;
    departementGateProjectId: number;
}



export interface ActionDto {
  actionLabel: string;
  comments: string[];
  requiredActionId: number;
}


export interface ReturnImpactDto {
  responseToActionLabel: string;
  validationStatus: string;
  justificationStatus: string
  requiredActionId: number;
}


export interface Action {
  id: number;
  actionLabel: string;
  responseToActionLabel: string;
  comments: string[];
  actionCreatedBy: string;
  impactSenderEmail: string;
  actionDocument: string;
  responseDocument: string;
  validationStatus: string;
  justificationStatus: string;
  validatedBy: string;
  responseEmailSender: string;
  requiredActionId: number;
  createdAt: string;
  lastModifiedAt: string;
}



