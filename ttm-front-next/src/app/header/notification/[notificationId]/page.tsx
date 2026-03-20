"use client";

import React, { useEffect, useState } from "react";
import { getNotificationById } from "@/axios/NotificationApis";
import { useParams } from "next/navigation";

export default function NotificationPage() {
  const [content, setContent] = useState("<p>Loading...</p>");
  const params = useParams();

  useEffect(() => {
    const notificationId = params?.notificationId;

    if (!notificationId) {
      setContent("<p style='color: red;'>Notification ID is missing.</p>");
      return;
    }

    const fetchNotification = async () => {
      try {
        const id = Number(notificationId);
        const response = await getNotificationById(id);
        const notification = response.data;

        setContent(notification.content);
      } catch (error) {
        if (error instanceof Error) {
          setContent(
            `<p style='color: red;'>Failed to load notification content: ${error.message}</p>`
          );
        } else {
          setContent("<p style='color: red;'>An unknown error occurred.</p>");
        }
      }
    };

    fetchNotification();
  }, [params]);

  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <iframe
        title="Notification"
        srcDoc={content}
        className="w-full max-w-5xl h-[80vh] border rounded"
      />
    </div>
  );
}
