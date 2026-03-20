"use client";

import React, { useEffect, useState } from "react";
import { getUsernameFromToken } from "@/app/services/SpocAffectationProjectService";
import { DocumentDto } from "@/types/document";
import {
  getDocumentsByAuthorUsername,
  filterDocuments,
  downloadFile,
} from "@/axios/DocumentApis";
import Badge from "@/components/ui/badge/Badge";
import {
  Table,
  TableBody,
  TableCell,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import Image from "next/image";
import { Download } from "lucide-react";
import Backdrop from "@mui/material/Backdrop";
import CircularProgress from "@mui/material/CircularProgress";
//import Button from "@/components/ui/button/Button";

export default function AddedImpactDocuments() {
  const [documents, setDocuments] = useState<DocumentDto[]>([]);
  const [originalDocuments, setOriginalDocuments] = useState<DocumentDto[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedGateLabel, setSelectedGateLabel] = useState<string>("");
  const [username, setUsername] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(true);

  const [currentPage, setCurrentPage] = useState(1);
  const documentsPerPage = 10;

  const gateLabels = ["T_2", "T_2Bis", "T_1", "T_1Ter", "T0", "T0Bis", "T0Ter", "T1", "T2", "T3", "T3Bis", "T3Ter", "T4", "T5", "T6"];

  const handleClose = () => setOpen(false);

  useEffect(() => {
    const fetchInitialDocuments = async () => {
      const usernameFromToken = getUsernameFromToken();
      if (!usernameFromToken) return;

      setUsername(usernameFromToken);
      setLoading(true);
      try {
        const docs = await getDocumentsByAuthorUsername(usernameFromToken);
        setOriginalDocuments(docs);
        setDocuments(docs);
      } catch (error) {
        console.error("Error fetching documents:", error);
      } finally {
        setLoading(false);
        setOpen(false);
      }
    };

    fetchInitialDocuments();
  }, []);

  useEffect(() => {
    const applyGateFilter = async () => {
      if (selectedGateLabel && username) {
        try {
          const { data } = await filterDocuments(
            undefined,
            username,
            selectedGateLabel
          );
          setDocuments(data);
          setCurrentPage(1);
        } catch (error) {
          console.error("Error filtering documents by gate:", error);
        }
      } else {
        setDocuments(originalDocuments);
        setCurrentPage(1);
      }
    };

    applyGateFilter();
  }, [selectedGateLabel, username]);

  const filteredDocuments = documents
    .filter((doc) =>
      doc.actionLabel.toLowerCase().includes(searchTerm.toLowerCase())
    )
    .sort(
      (a, b) =>
        new Date(b.dateUpload).getTime() - new Date(a.dateUpload).getTime()
    );

  const totalPages = Math.ceil(filteredDocuments.length / documentsPerPage);
  const startIndex = (currentPage - 1) * documentsPerPage;
  const currentDocuments = filteredDocuments.slice(
    startIndex,
    startIndex + documentsPerPage
  );

  if (loading) {
    return (
      <Backdrop
        sx={(theme) => ({
          color: "#ab3c73",
          backgroundColor: "#FFF",
          zIndex: theme.zIndex.drawer + 1,
        })}
        open={open}
        onClick={handleClose}
      >
        <CircularProgress color="inherit" />
      </Backdrop>
    );
  }

  return (
    <div className="pt-6 px-4 pb-24">
      {/* Search & Filter */}
      <div className="relative flex justify-between items-center px-4 w-full mb-4">
        <div className="w-2/5">
          <div className="p-4 flex items-center">
            <input
              type="text"
              placeholder="Rechercher par titre..."
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value);
                setCurrentPage(1);
              }}
              className="bg-white w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-blue-500 text-sm dark:bg-white/[0.05] dark:text-white"
            />
            <Image
              src="/images/icons/search.png"
              alt="search-bar"
              width={40}
              height={40}
              className="w-4 h-4 relative right-8"
            />
          </div>
        </div>

        <div className="w-1/4">
          <select
            value={selectedGateLabel}
            onChange={(e) => setSelectedGateLabel(e.target.value)}
            className="w-full px-4 py-2 border border-gray-200 rounded-lg bg-white text-sm focus:outline-none focus:ring-1 focus:ring-blue-500 dark:bg-white/[0.05] dark:text-white"
          >
            <option value="">Tous les gates</option>
            {gateLabels.map((label, idx) => (
              <option key={idx} value={label}>
                {label}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Table */}
      <div className="overflow-hidden rounded-xl border border-gray-200 bg-white dark:border-white/[0.05] dark:bg-white/[0.03]">
        <div className="w-full overflow-x-auto">
          <div className="min-w-[700px] mx-auto">
            <Table>
              <TableHeader className="border-b border-gray-100 dark:border-white/[0.05]">
                <TableRow className="bg-gray-50 dark:bg-white/[0.03]">
                  {["ActionLabel", "Document Type", "Gate", "Date Upload", "Size", "Download"].map(
                    (head, idx) => (
                      <TableCell
                        key={idx}
                        isHeader
                        className="px-4 py-2 font-semibold text-gray-600 text-sm text-start dark:text-gray-400"
                      >
                        {head}
                      </TableCell>
                    )
                  )}
                </TableRow>
              </TableHeader>

              <TableBody className="divide-y divide-gray-100 dark:divide-white/[0.05]">
                {currentDocuments.length > 0 ? (
                  currentDocuments.map((doc, index) => (
                    <TableRow
                      key={index}
                      className="hover:bg-gray-50 dark:hover:bg-white/[0.05]"
                    >
                      <TableCell className="px-4 py-3 text-gray-700 text-sm dark:text-white/90">
                        {doc.actionLabel}
                      </TableCell>

                      <TableCell className="px-4 py-4 text-start">
                        <Badge
                          size="sm"
                          variant="light"
                          className={`text-sm ${
                            doc.typeDocument.replace(".", "").toUpperCase() === "PDF"
                              ? "bg-blue-100 text-blue-600"
                              : doc.typeDocument.replace(".", "").toUpperCase() === "DOCX"
                              ? "bg-yellow-100 text-yellow-600"
                              : doc.typeDocument.replace(".", "").toUpperCase() === "PPTX"
                              ? "bg-red-100 text-red-600"
                              : "bg-gray-100 text-gray-600"
                          } dark:bg-white/[0.05]`}
                        >
                          {doc.typeDocument.replace(".", "").toUpperCase()}
                        </Badge>
                      </TableCell>

                      <TableCell className="px-4 py-3 text-gray-600 text-sm dark:text-gray-400">
                        {doc.gateLabel}
                      </TableCell>

                      <TableCell className="px-4 py-3 text-gray-600 text-sm dark:text-gray-400">
                        {doc.dateUpload
                          ? new Date(doc.dateUpload).toISOString().slice(0, 16).replace("T", " ")
                          : "—"}
                      </TableCell>

                      <TableCell className="px-4 py-3 text-gray-600 text-sm dark:text-gray-400">
                        {doc.size}
                      </TableCell>

                      <TableCell className="px-4 py-3 text-blue-600 hover:underline text-sm flex items-center gap-2">
                        <button
                          onClick={() => downloadFile(doc.documentLabel)}
                          className="flex items-center focus:outline-none"
                        >
                          <Download className="w-4 h-4 mr-1" />
                        </button>
                      </TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell
                      colSpan={6}
                      className="px-4 py-6 text-center text-gray-500 dark:text-gray-400"
                    >
                      No document found.
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </div>
        </div>
      </div>

      {/* Pagination */}
        {totalPages > 1 && (
          <div className="mt-8 flex justify-center items-center flex-wrap gap-3">
            {Array.from({ length: totalPages }, (_, index) => {
              const isActive = currentPage === index + 1;
              return (
                <button
                  key={index + 1}
                  onClick={() => setCurrentPage(index + 1)}
                  className={`px-4 py-2 text-sm font-medium rounded-full border transition-all duration-300 ease-in-out shadow-sm ${
                    isActive
                      ? "bg-[#B12B89] text-white border-[#B12B89] scale-105 shadow-lg"
                      : "bg-white text-[#B12B89] border-[#B12B89] hover:bg-[#B12B89]/10 hover:scale-105"
                  }`}
                >
                  {index + 1}
                </button>
              );
            })}
          </div>
        )}

    </div>
  );
}
