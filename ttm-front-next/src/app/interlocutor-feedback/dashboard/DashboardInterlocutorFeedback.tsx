"use client";

import React, { useEffect, useState } from "react";
import Backdrop from "@mui/material/Backdrop";
import CircularProgress from "@mui/material/CircularProgress";
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

import {
  getTrackingPercentages,
  getMarketTypePercentages,
  getProjectTypePercentages,
} from "@/axios/DashboardApis";

import {
  PieTrackingDto,
  PieMarketTypeDto,
  PieProjectTypeDto,
} from "@/types/dashboard";

const COLORS = ["#0088FE", "#00C49F", "#FFBB28", "#FF8042", "#FF6666", "#AAFF99"];

const DashboardInterlocutorFeedback = () => {
  const [trackingData, setTrackingData] = useState<PieTrackingDto[]>([]);
  const [marketData, setMarketData] = useState<PieMarketTypeDto[]>([]);
  const [projectData, setProjectData] = useState<PieProjectTypeDto[]>([]);
  const [loading, setLoading] = useState(true); // <- set to true initially
  const [open, setOpen] = useState(true);

  useEffect(() => {
    Promise.all([
      getTrackingPercentages().then((res) => setTrackingData(res.data)),
      getMarketTypePercentages().then((res) => setMarketData(res.data)),
      getProjectTypePercentages().then((res) => setProjectData(res.data)),
    ])
      .catch(console.error)
      .finally(() => {
        setLoading(false);
        setOpen(false);
      });
  }, []);

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
                  label={false}
                >
                  {trackingData.map((_, index) => (
                    <Cell
                      key={`cell-${index}`}
                      fill={COLORS[index % COLORS.length]}
                    />
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

      
    </div>
  );
};

export default DashboardInterlocutorFeedback;
