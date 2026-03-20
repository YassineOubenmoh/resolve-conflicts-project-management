import axios from 'axios';
import { setupAuthInterceptors } from './authInterceptor';

const notificationApi = axios.create({
  baseURL: 'http://localhost:8085/api/internal', // Adjust baseURL as needed
});


// Access & refresh token configuration
setupAuthInterceptors(notificationApi);





export const getUnconsumedNotificationsByReceiverEmail = (receiverEmail: string) => {
  return notificationApi.get(`/unconsumed-by-receiver/${encodeURIComponent(receiverEmail)}`);
};

export const getNotificationById = (id: number) => {
  console.log(`Calling GET /find/${id}`);
  return notificationApi.get(`/find/${id}`);
};


export const markAsConsumed = (id: number) => {
  console.log(`Calling PUT /consume/${id}`);
  return notificationApi.put(`/consume/${id}`);
};


export const getAllNotifications = () => {
  console.log("Calling GET /notifs");
  return notificationApi.get("/notifs");
};
