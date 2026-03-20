'use client';
import React, { useEffect, useState } from "react";
import {
    Table,
    TableBody,
    TableCell,
    TableHeader,
    TableRow,
} from "../../../components/ui/table";
import Badge from "../../../components/ui/badge/Badge";
import Image from "next/image";
import { Download } from "lucide-react";
import { getActionsDocumentsByProjectId, downloadFile } from "@/axios/DocumentApis";
import { useSearchParams } from "next/navigation";

interface ActionDocument {
    id: number;
    actionLabel: string;
    projectId: number;
    requiredActionId: number;
    documentLabel: string;
    typeDocument: string;
    department: string;
    authorName: string;
    gateLabel: string;
    dateUpload: string;
    size: string;
}

interface DocumentRow {
    id: number;
    actionLabel: string;
    documentLabel: string;
    type: string;
    department: string;
    author: string;
    gate: string;
    updatedAt: string;
    size: string;
}

export default function DocumentTable() {
    const [searchTerm, setSearchTerm] = useState("");
    const [documents, setDocuments] = useState<DocumentRow[]>([]);
    const [loading, setLoading] = useState(false);
    const searchParams = useSearchParams();
    const projectId = Number(searchParams.get("id"));

    // Fetch documents by project ID using the actions documents endpoint
    useEffect(() => {
        const fetchActionDocuments = async () => {
            if (!projectId) return;

            setLoading(true);
            try {
                const response = await getActionsDocumentsByProjectId(projectId);
                const data = response.data;

                // Ensure the response is an array before mapping
                if (Array.isArray(data)) {
                    const mappedDocuments: DocumentRow[] = data.map((doc: ActionDocument) => ({
                        id: doc.id,
                        actionLabel: doc.actionLabel,
                        documentLabel: doc.documentLabel,
                        type: doc.typeDocument.replace('.', '').toUpperCase(),
                        department: doc.department,
                        author: doc.authorName,
                        gate: doc.gateLabel,
                        updatedAt: formatDate(doc.dateUpload),
                        size: doc.size
                    }));

                    setDocuments(mappedDocuments);
                } else {
                    console.warn("Expected an array for action documents, but got:", data);
                    setDocuments([]);
                }
            } catch (err) {
                console.error("Error loading action documents:", err);
                setDocuments([]);
            } finally {
                setLoading(false);
            }
        };

        fetchActionDocuments();
    }, [projectId]);


    const formatDate = (dateString: string): string => {
        try {
            const date = new Date(dateString);
            return date.toLocaleDateString('fr-FR', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            });
        } catch {
            return dateString;
        }
    };

    const handleDownload = async (documentName: string) => {
        try {
            await downloadFile(documentName);
        } catch (error) {
            console.error("Error downloading document:", error);
        }
    };

    const filteredDocuments = documents.filter(doc =>
        doc.actionLabel.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const getIconForType = (type: string) => {
        switch (type.toUpperCase()) {
            case "PDF":
                return "/images/icons/pdf.png";
            case "PPT":
            case "PPTX":
                return "/images/icons/ppt-icon.jpg";
            case "DOC":
            case "DOCX":
                return "/images/icons/word-icon.png";
            default:
                return "/images/icons/file.png";
        }
    };

    const badgeColors = [
        "success",
        "info",
        "purple",
        "danger",
        "warning",
    ];

    // Generate a deterministic pseudo-hash to pick colors for author
    const getAuthorBadgeColor = (author: string) => {
        if (!author) return "secondary";
        let hash = 0;
        for (let i = 0; i < author.length; i++) {
            hash = author.charCodeAt(i) + ((hash << 5) - hash);
        }
        const index = Math.abs(hash) % badgeColors.length;
        return badgeColors[index];
    };

    // Use the same color logic for gate badges, based on badgeColors array for good color distribution
    const getGateBadgeColor = (gate: string) => {
        if (!gate) return "secondary";
        let hash = 0;
        for (let i = 0; i < gate.length; i++) {
            hash = gate.charCodeAt(i) + ((hash << 5) - hash);
        }
        const index = Math.abs(hash) % badgeColors.length;
        return badgeColors[index];
    };

    return (
        <div className="pt-3.5 pb-24">
            {/* Search Bar */}
            <div className="relative right-4 w-2/5">
                <div className="p-4 flex items-center">
                    <input
                        type="text"
                        placeholder="Search for documents..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="bg-white w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-blue-500 text-sm dark:bg-white/[0.05] dark:text-white"
                    />
                    <Image
                        src={"/images/icons/search.png"}
                        alt="search"
                        width={16}
                        height={16}
                        className="w-4 h-4 relative right-8"
                    />
                </div>
            </div>

            {/* Table */}
            <div className="overflow-hidden rounded-xl border border-gray-200 bg-white dark:border-white/[0.05] dark:bg-white/[0.03]">
                <div className="w-full overflow-x-auto">
                    <div className="min-w-[1200px] mx-auto">
                        <Table>
                            <TableHeader className="border-b border-gray-100 dark:border-white/[0.05]">
                                <TableRow className="bg-gray-50 dark:bg-white/[0.03]">
                                    {["Action Name", "Type", "Department", "Auteur", "Gate", "Updated At", "Taille"].map((head, idx) => (
                                        <TableCell key={idx} isHeader className="px-12 py-3 font-semibold text-gray-600 text-sm text-start dark:text-gray-400">
                                            {head}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            </TableHeader>

                            <TableBody className="divide-y divide-gray-100 dark:divide-white/[0.05]">
                                {filteredDocuments.map((doc, index) => (
                                    <TableRow key={index} className="hover:bg-gray-50 dark:hover:bg-white/[0.05]">
                                        {/* Nom */}
                                        <TableCell className="pl-8 py-4 text-start">
                                            <span className="text-gray-900 text-sm font-medium dark:text-white/90">{doc.actionLabel}</span>
                                        </TableCell>

                                        {/* Type */}
                                        <TableCell className="pl-9 py-4 text-start">
                                            <div className="flex items-center gap-2">
                                                <Image src={getIconForType(doc.type)} width={18} height={18} alt={doc.type} />
                                                <span className="text-gray-700 text-sm dark:text-white/90">{doc.type}</span>
                                            </div>
                                        </TableCell>

                                        {/* Département */}
                                        <TableCell className="pl-14 py-4 text-start">
                                            <span className="text-gray-500 text-sm dark:text-gray-400">{doc.department}</span>
                                        </TableCell>

                                        {/* Auteur */}
                                        <TableCell className="pl-10 py-4 text-start">
                                            <Badge size="sm" color={getAuthorBadgeColor(doc.author)}>
                                                {doc.author}
                                            </Badge>
                                        </TableCell>

                                        {/* Gate */}
                                        <TableCell className="pl-12 py-4 text-start">
                                            <Badge size="sm" color={getGateBadgeColor(doc.gate)}>
                                                {doc.gate}
                                            </Badge>
                                        </TableCell>

                                        {/* Date modifié */}
                                        <TableCell className="pl-10 py-4 text-start">
                                            <span className="text-gray-500 text-sm dark:text-gray-400">{doc.updatedAt}</span>
                                        </TableCell>

                                        {/* Taille */}
                                        <TableCell className="px-4 py-4 text-start">
                                            <div className="flex items-center justify-between">
                                                <span className="text-gray-500 text-sm dark:text-gray-400">{doc.size}</span>
                                                <Download onClick={() => handleDownload(doc.documentLabel)} className="w-4 h-4 text-gray-400 cursor-pointer hover:text-blue-500 transition-colors" />
                                            </div>
                                        </TableCell>
                                    </TableRow>
                                ))}

                                {filteredDocuments.length === 0 && !loading && (
                                    <TableRow>
                                        <TableCell colSpan={8} className="px-4 py-8 text-center text-gray-500 dark:text-gray-400">
                                            Aucun document trouvé
                                        </TableCell>
                                    </TableRow>
                                )}

                                {loading && (
                                    <TableRow>
                                        <TableCell colSpan={8} className="px-4 py-8 text-center text-gray-500 dark:text-gray-400">
                                            Chargement des documents...
                                        </TableCell>
                                    </TableRow>
                                )}
                            </TableBody>
                        </Table>
                    </div>
                </div>
            </div>
        </div>
    );
}

