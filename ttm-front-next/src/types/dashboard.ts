export interface PieMarketTypeDto {
  marketType: string;
  marketTypePercentage: number;
}


export interface PieProjectTypeDto {
  projectType: number;
  projectTypePercentage: string;
}


export interface PieTrackingDto {
  trackingType: string;
  trackingPercentage: number;
}

export interface ImpactFeedbackDto  {
  impactFeedback: string;
  impactFeedbackPercentage: number;
}

export interface HistogramProjectTtmDto {
  projectTitle: string;
  ttm: number;
}