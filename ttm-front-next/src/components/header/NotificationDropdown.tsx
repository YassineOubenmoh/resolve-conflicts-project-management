"use client";

import { Clock } from "lucide-react";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Dropdown } from "@/components/ui/dropdown/Dropdown";
import { DropdownItem } from "@/components/ui/dropdown/DropdownItem";
import {
  getAllNotifications,
  getUnconsumedNotificationsByReceiverEmail,
  markAsConsumed,
} from "@/axios/NotificationApis";
import { Notification } from "@/types/project";
import { getUsernameFromToken } from "@/app/services/SpocAffectationProjectService";
import { getUserByUsername } from "@/axios/UsersApis";

export default function NotificationDropdown() {
  const router = useRouter();
  const [isOpen, setIsOpen] = useState(false);
  const [notifying, setNotifying] = useState(true);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [userEmail, setUserEmail] = useState<string | null>(null);
  const [showAll, setShowAll] = useState(false);
  const [loading, setLoading] = useState(false);

  const toggleDropdown = () => setIsOpen(!isOpen);
  const closeDropdown = () => setIsOpen(false);

  const handleClick = () => {
    toggleDropdown();
    setNotifying(false);
  };

  const extractTitleFromContent = (content: string): string => {
    try {
      const parser = new DOMParser();
      const doc = parser.parseFromString(content, "text/html");
      const titleTag = doc.querySelector("title");
      return titleTag?.textContent?.trim() || "No title";
    } catch {
      return "No title";
    }
  };

  useEffect(() => {
    const fetchUserEmail = async () => {
      try {
        setLoading(true);
        const username = getUsernameFromToken();
        if (!username) return;
        const { data: user } = await getUserByUsername(username);
        setUserEmail(user?.email ?? null);
      } catch (err) {
        console.error("Error fetching user email:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchUserEmail();
  }, []);

  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        setLoading(true);
        let data: Notification[] = [];
        if (showAll) {
          ({ data } = await getAllNotifications());
        } else if (userEmail) {
          ({ data } = await getUnconsumedNotificationsByReceiverEmail(userEmail));
        }
        const sortedNotifications = data.sort(
          (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
        setNotifications(sortedNotifications);
      } catch (err) {
        console.error("Error fetching notifications:", err);
      } finally {
        setLoading(false);
      }
    };

    if (isOpen) {
      fetchNotifications();
    }
  }, [isOpen, showAll, userEmail]);

  const handleNotificationClick = async (id: number) => {
    closeDropdown();
    try {
      await markAsConsumed(id);
    } catch (err) {
      console.warn("Failed to mark as consumed:", err);
    } finally {
      router.push(`/header/notification/${id}`);
    }
  };

  const handleViewAll = () => {
    setShowAll(true);
  };

  return (
    <div className="relative">
      <button
        className="relative flex items-center justify-center text-gray-500 bg-white border border-gray-200 rounded-full h-11 w-11 hover:text-gray-700 hover:bg-gray-100 dark:border-gray-800 dark:bg-gray-900 dark:text-gray-400 dark:hover:bg-gray-800 dark:hover:text-white"
        onClick={handleClick}
      >
        <span className={`absolute right-0 top-0.5 z-10 h-2 w-2 rounded-full bg-orange-400 ${!notifying ? "hidden" : "flex"}`}>
          <span className="absolute inline-flex w-full h-full bg-orange-400 rounded-full opacity-75 animate-ping" />
        </span>
        <svg className="fill-current" width="20" height="20" viewBox="0 0 20 20">
          <path
            d="M10.75 2.29248C10.75 1.87827 10.4143 1.54248 10 1.54248C9.58583 1.54248 9.25004 1.87827 9.25004 2.29248V2.83613C6.08266 3.20733 3.62504 5.9004 3.62504 9.16748V14.4591H3.33337C2.91916 14.4591 2.58337 14.7949 2.58337 15.2091C2.58337 15.6234 2.91916 15.9591 3.33337 15.9591H4.37504H15.625H16.6667C17.0809 15.9591 17.4167 15.6234 17.4167 15.2091C17.4167 14.7949 17.0809 14.4591 16.6667 14.4591H16.375V9.16748C16.375 5.9004 13.9174 3.20733 10.75 2.83613V2.29248ZM14.875 14.4591V9.16748C14.875 6.47509 12.6924 4.29248 10 4.29248C7.30765 4.29248 5.12504 6.47509 5.12504 9.16748V14.4591H14.875ZM8.00004 17.7085C8.00004 18.1228 8.33583 18.4585 8.75004 18.4585H11.25C11.6643 18.4585 12 18.1228 12 17.7085C12 17.2943 11.6643 16.9585 11.25 16.9585H8.75004C8.33583 16.9585 8.00004 17.2943 8.00004 17.7085Z"
            fill="currentColor"
          />
        </svg>
      </button>

      <Dropdown
        isOpen={isOpen}
        onClose={() => {
          setShowAll(false);
          closeDropdown();
        }}
        className="absolute -right-[240px] mt-[17px] flex h-[480px] w-[350px] flex-col rounded-2xl border border-gray-200 bg-white p-3 shadow-theme-lg dark:border-gray-800 dark:bg-gray-dark sm:w-[361px] lg:right-0"
      >
        {/* Linear Loading Bar */}
        {loading && (
          <div className="h-1.5 w-full overflow-hidden rounded bg-gray-200 mb-3 dark:bg-gray-700">
            <div className="h-full w-full animate-pulse" style={{ backgroundColor: "#B12B89" }} />
          </div>
        )}


        <div className="flex items-center justify-between pb-3 mb-3 border-b border-gray-100 dark:border-gray-700">
          <h5 className="text-lg font-semibold text-gray-800 dark:text-gray-200">Notification</h5>
          <button
            onClick={toggleDropdown}
            className="text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200"
          >
            ✕
          </button>
        </div>

        <ul className="flex flex-col h-auto overflow-y-auto custom-scrollbar">
          {notifications.length === 0 && !loading ? (
            <li className="px-4 py-2 text-center text-gray-500 dark:text-gray-400">
              No notifications.
            </li>
          ) : (
            notifications.map((notif) => (
              <li key={notif.id}>
                <DropdownItem
                  className="flex gap-3 border-b border-gray-100 p-3 hover:bg-gray-100 dark:border-gray-800 dark:hover:bg-white/5 rounded-lg"
                  onClick={() => handleNotificationClick(notif.id)}
                >
                  <div>
                    <div className="text-sm font-medium text-gray-800 dark:text-white">
                      {notif.senderMail}
                    </div>
                    <div className="text-sm text-gray-700 dark:text-white/80">
                      {extractTitleFromContent(notif.content)}
                    </div>
                    <div className="flex items-center gap-1 text-xs text-gray-500 dark:text-gray-400">
                      <Clock className="w-4 h-4" />
                      {new Date(notif.createdAt).toLocaleString()}
                    </div>
                  </div>
                </DropdownItem>
              </li>
            ))
          )}
        </ul>

        {!showAll && !loading && (
          <button
            onClick={handleViewAll}
            className="block mt-3 px-4 py-2 text-sm text-center font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-100 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:hover:bg-gray-700"
          >
            View All Notifications
          </button>
        )}
      </Dropdown>
    </div>
  );
}
