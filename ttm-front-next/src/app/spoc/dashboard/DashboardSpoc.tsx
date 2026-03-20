"use client";

import React, { useEffect, useState } from "react";
import Backdrop from "@mui/material/Backdrop";
import CircularProgress from "@mui/material/CircularProgress";

import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs, { Dayjs } from 'dayjs';



import {
  getTrackingPercentages,
  getMarketTypePercentages,
  getProjectTypePercentages,
  getImpactFeedbackPercentages,
  getTtmProjectsByDepartment,
  
} from "@/axios/DashboardApis";



import {
  PieTrackingDto,
  PieMarketTypeDto,
  PieProjectTypeDto,
  ImpactFeedbackDto,
  HistogramProjectTtmDto,
} from "@/types/dashboard";

import {
  PieChart,
  Pie,
  Cell,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

import { getUsernameFromToken } from "@/app/services/SpocAffectationProjectService";
import { getUserByUsername } from "@/axios/UsersApis";

import { RefreshCcw } from "lucide-react";

const COLORS = ["#0088FE", "#00C49F", "#FFBB28", "#FF8042", "#FF6666", "#AAFF99"];

const DashboardSpoc = () => {
  const [trackingData, setTrackingData] = useState<PieTrackingDto[]>([]);
  const [marketData, setMarketData] = useState<PieMarketTypeDto[]>([]);
  const [projectData, setProjectData] = useState<PieProjectTypeDto[]>([]);
  const [impactFeedbackData, setImpactFeedbackData] = useState<ImpactFeedbackDto[]>([]);
  const [ttmData, setTtmData] = useState<HistogramProjectTtmDto[]>([]);

  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(true);

  const [department, setDepartment] = useState<string | null>(null);

  const [fromDate, setFromDate] = useState<Dayjs | null>(dayjs().subtract(30, 'day'));
  const [toDate, setToDate] = useState<Dayjs | null>(dayjs());


  useEffect(() => {
    const fetchUserData = async () => {
      const username = getUsernameFromToken();
      if (!username) return;

      try {
        const { data: user } = await getUserByUsername(username);
        if (!user?.department) return;
        setDepartment(user.department);
      } catch (error) {
        console.error("Error fetching user:", error);
      }
    };

    fetchUserData();
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      try {
        await Promise.all([
          getTrackingPercentages().then((res) => setTrackingData(res.data)),
          getMarketTypePercentages().then((res) => setMarketData(res.data)),
          getProjectTypePercentages().then((res) => setProjectData(res.data)),
          getImpactFeedbackPercentages().then((res) => setImpactFeedbackData(res.data)),
        ]);

        if (department) {
          const { data: ttm } = await getTtmProjectsByDepartment(department);
          setTtmData(ttm);
        }
      } catch (error) {
        console.error("Dashboard fetch error:", error);
      } finally {
        setLoading(false);
        setOpen(false);
      }
    };

    if (department) {
      fetchData();
    }
  }, [department]);

  if (loading) {
    return (
      <Backdrop
        sx={(theme) => ({
          color: "#ab3c73",
          backgroundColor: "#FFF",
          zIndex: theme.zIndex.drawer + 1,
        })}
        open={open}
      >
        <CircularProgress color="inherit" />
      </Backdrop>
    );
  }

  return (
    <>

<div className="px-6">
    <LocalizationProvider dateAdapter={AdapterDayjs}>
  <div className="mb-4 flex items-end gap-3">
    <div className="flex flex-col text-xs text-gray-700">
      <label className="mb-1">From</label>
      <DatePicker
        value={fromDate}
        onChange={(newValue) => setFromDate(newValue)}
        format="YYYY-MM-DD"
        slotProps={{ textField: { size: 'small' } }}
      />
    </div>
    <div className="flex flex-col text-xs text-gray-700">
      <label className="mb-1">To</label>
      <DatePicker
        value={toDate}
        onChange={(newValue) => setToDate(newValue)}
        format="YYYY-MM-DD"
        minDate={fromDate}
        slotProps={{ textField: { size: 'small' } }}
      />
    </div>
    <button
      type="button"
      onClick={() => {
        setFromDate(dayjs().subtract(30, 'day'));
        setToDate(dayjs());
      }}
      className="flex items-center gap-1 px-3 py-1 text-xs font-semibold text-white rounded hover:bg-[#8F2269] transition"
      style={{ backgroundColor: '#B12B89', height: '38px' }}
      aria-label="Reset dates"
    >
      <RefreshCcw className="w-4 h-4" />
      Reset
    </button>
  </div>
</LocalizationProvider>

    





    <div className="p-6 grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">



      {/* Tracking Pie Chart */}
      <div className="bg-white shadow rounded-2xl p-4">
        <h2 className="text-lg font-semibold mb-2">Tracking Percentages</h2>
        {trackingData.length > 0 ? (
          <>
            <div className="flex flex-wrap gap-4 mb-4">
              {trackingData.map((entry, index) => (
                <div key={index} className="flex items-center space-x-2">
                  <div
                    className="w-4 h-4 rounded-full"
                    style={{ backgroundColor: COLORS[index % COLORS.length] }}
                  ></div>
                  <span className="text-sm text-gray-700 font-medium">
                    {entry.trackingType}
                  </span>
                </div>
              ))}
            </div>
            <ResponsiveContainer width="100%" height={250}>
              <PieChart>
                <Pie
                  data={trackingData}
                  dataKey="trackingPercentage"
                  nameKey="trackingType"
                  cx="50%"
                  cy="50%"
                  outerRadius={90}
                >
                  {trackingData.map((_, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(value: number) => `${value.toFixed(2)}%`} />
              </PieChart>
            </ResponsiveContainer>
          </>
        ) : (
          <p className="text-center text-gray-500">No tracking data available.</p>
        )}
      </div>

      {/* Market Type Histogram */}
      <div className="bg-white shadow rounded-2xl p-4">
        <h2 className="text-lg font-semibold mb-4">Market Type Percentages</h2>
        {marketData.length > 0 ? (
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={marketData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="marketType" />
              <YAxis domain={[0, 100]} tickFormatter={(val) => `${val}%`} />
              <Tooltip formatter={(value: number) => `${value.toFixed(2)}%`} />
              <Legend />
              <Bar dataKey="marketTypePercentage" fill="#8884d8" />
            </BarChart>
          </ResponsiveContainer>
        ) : (
          <p className="text-center text-gray-500">No market data available.</p>
        )}
      </div>

      {/* Project Type Histogram */}
      <div className="bg-white shadow rounded-2xl p-4">
        <h2 className="text-lg font-semibold mb-4">Project Type Percentages</h2>
        {projectData.length > 0 ? (
          <ResponsiveContainer width="100%" height={250}>
            <BarChart
              data={projectData.map((item) => ({
                ...item,
                projectTypePercentage: parseFloat(item.projectTypePercentage),
              }))}
            >
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="projectType" />
              <YAxis domain={[0, 100]} tickFormatter={(val) => `${val}%`} />
              <Tooltip formatter={(value: number) => `${value.toFixed(2)}%`} />
              <Legend />
              <Bar dataKey="projectTypePercentage" fill="#82ca9d" />
            </BarChart>
          </ResponsiveContainer>
        ) : (
          <p className="text-center text-gray-500">No project data available.</p>
        )}
      </div>

      {/* Impact Feedback Histogram */}
      <div className="bg-white shadow rounded-2xl p-4">
        <h2 className="text-lg font-semibold mb-4">Impact Feedback Percentages</h2>
        {impactFeedbackData.length > 0 ? (
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={impactFeedbackData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="impactFeedback" />
              <YAxis domain={[0, 100]} tickFormatter={(val) => `${val}%`} />
              <Tooltip formatter={(value: number) => `${value.toFixed(2)}%`} />
              <Legend />
              <Bar dataKey="impactFeedbackPercentage" fill="#ffc658" />
            </BarChart>
          </ResponsiveContainer>
        ) : (
          <p className="text-center text-gray-500">No impact feedback data available.</p>
        )}
      </div>

      {/* TTM Project Details */}
{ttmData.length > 0 && (
  <div className="bg-white rounded-2xl p-6 shadow-md w-full max-w-6xl mx-auto">
    <h2 className="text-xl font-bold mb-6 text-black tracking-wide select-none">
      Project Time to Market Details
    </h2>

    <table className="w-full table-auto border-collapse">
      <thead>
        <tr className="bg-[#B12B89]">
          <th className="px-4 py-3 text-left text-sm font-semibold text-white uppercase tracking-wide w-2/3">
            Project
          </th>
          <th className="px-4 py-3 text-left text-sm font-semibold text-white uppercase tracking-wide w-1/3 flex items-center space-x-1">
            <span>Ttm</span>
            {/* Clock Icon */}
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-4 w-4 text-white opacity-80"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              strokeWidth={2}
              aria-hidden="true"
            >
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 6v6l4 2" />
              <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth={2} />
            </svg>
          </th>
        </tr>
      </thead>
      <tbody>
        {ttmData
          .filter((item: HistogramProjectTtmDto) => item.projectTitle && item.ttm !== undefined)
          .map((project: HistogramProjectTtmDto, index: number) => (
            <tr
              key={index}
              className="hover:bg-[#f7e6f1] transition-colors cursor-default select-text"
            >
              <td className="px-4 py-3 text-sm font-medium text-gray-900 break-words">
                {project.projectTitle}
              </td>
              <td className="px-4 py-3 text-sm text-[#B12B89] font-semibold">
                {project.ttm} days
              </td>
            </tr>
          ))}
      </tbody>
    </table>
  </div>
)}





    </div>
</div>
    </>
  );

  
};

export default DashboardSpoc;
