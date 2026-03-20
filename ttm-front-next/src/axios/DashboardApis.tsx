import axios from "axios";
import { setupAuthInterceptors } from "./authInterceptor";

export const dashboardApi = axios.create({
  baseURL: "http://localhost:8080/dashboard",
});




// Access & refresh token configuration
setupAuthInterceptors(dashboardApi);





export const getTrackingPercentages = () => {
  console.log("Calling GET /pie-tracking");
  return dashboardApi.get("/pie-tracking");
};


export const getMarketTypePercentages = () => {
  console.log("Calling GET /pie-market");
  return dashboardApi.get("/pie-market");
};


export const getProjectTypePercentages = () => {
  console.log("Calling GET /pie-project");
  return dashboardApi.get("/pie-project");
};

export const getImpactFeedbackPercentages = () => {
  console.log("Calling GET /histogram-feedbacks");
  return dashboardApi.get("/histogram-feedbacks");
};


export const getTtmProjectsByDepartment = (department: string) => {
  console.log(`Calling GET /histogram-ttm/${department}`);
  return dashboardApi.get(`/histogram-ttm/${department}`);
};


{/* 
export const getTrackingPercentagesBetweenDates = (dateFrom: string, dateTo: string) => {
  console.log(`Calling GET /pie-tracking/${dateFrom}/${dateTo}`);
  return dashboardApi.get(`/pie-tracking/${dateFrom}/${dateTo}`);
};
*/}




export const getTrackingPercentagesByDates = (fromDate: string, toDate: string, department: string) => {
  console.log(`Calling GET /pie-tracking-by-dates?fromDate=${fromDate}&toDate=${toDate}&department=${department}`);
  return dashboardApi.get("/pie-tracking-by-dates", {
    params: {
      fromDate,
      toDate,
      department,
    },
  });
};

export const getMarketTypePercentagesByDates = (fromDate: string, toDate: string, department: string) => {
  console.log(`Calling GET /pie-market-by-dates?fromDate=${fromDate}&toDate=${toDate}&department=${department}`);
  return dashboardApi.get("/pie-market-by-dates", {
    params: {
      fromDate,
      toDate,
      department,
    },
  });
};

export const getProjectTypePercentagesByDates = (fromDate: string, toDate: string, department: string) => {
  console.log(`Calling GET /pie-project-by-dates?fromDate=${fromDate}&toDate=${toDate}&department=${department}`);
  return dashboardApi.get("/pie-project-by-dates", {
    params: {
      fromDate,
      toDate,
      department,
    },
  });
};

export const getTtmProjectsByDates = (fromDate: string, toDate: string, department: string) => {
  console.log(`Calling GET /histogram-ttm-by-dates?fromDate=${fromDate}&toDate=${toDate}&department=${department}`);
  return dashboardApi.get("/histogram-ttm-by-dates", {
    params: {
      fromDate,
      toDate,
      department,
    },
  });
};