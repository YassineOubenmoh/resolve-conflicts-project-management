'use client';
import { getOwnerProjects, getTrackingById } from "@/axios/ProjectApis";
import { jwtDecode } from "jwt-decode";
import Image from "next/image";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";


import Button from "@/components/ui/button/Button";
import { BackendProject } from "@/types/project";
import Link from "next/link";
import Badge from "../../../components/ui/badge/Badge";
import {
    Table,
    TableBody,
    TableCell,
    TableHeader,
    TableRow,
} from "../../../components/ui/table";

import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';



interface JwtPayload {
    preferred_username?: string;
}


interface Project {
    id: string;
    owner: string;
    title: string;
    description: string;
    launchDate: Date;
    passageDate: Date;
    departments: string[];
    ownerMoa: string[];
    tracking: string;
    trackingLabel: string; // Added new field for tracking label
    gate: string;
}

export default function ProjectList() {
    const [user, setUser] = useState<JwtPayload>({});
    const [projects, setProjects] = useState<Project[]>([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [loading, setLoading] = useState(false);
    const [open, setOpen] = useState(true);
    const handleClose = () => {
        setOpen(false);
    };

    const router = useRouter();

    // Fetch tracking label by ID
    const fetchTrackingLabel = async (trackingId: string) => {
        if (!trackingId) return "";
        try {
            const response = await getTrackingById(trackingId);
            return response.data.trackingType || "no tracking";
        } catch (error) {
            console.error(`Error fetching tracking for ID ${trackingId}:`, error);
            return "";
        }
    };

    // Fetch Username from JWT

    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        if (token) {
            try {
                const decoded = jwtDecode<JwtPayload>(token);
                setUser(decoded);

            } catch (err) {
                console.error("Failed to decode JWT:", err);
            }
        }
    }, []);



    // Fetch projects from the API
    useEffect(() => {
        if (!user.preferred_username) return; // Wait until user.name is available

        const fetchProjects = async () => {
            setLoading(true);
            try {
                const backendProjects = await getOwnerProjects(user.preferred_username!);

                const initialProjects: Project[] = backendProjects.map((data: BackendProject) => ({
                    id: data.id.toString(),
                    owner: data.ownerFullName,
                    title: data.title,
                    description: data.description,
                    launchDate: new Date(data.dateStartTtm),
                    passageDate: new Date(),
                    departments: data.departments || [],
                    ownerMoa: data.moas || [],
                    tracking: data.trackingId?.toString() || "",
                    trackingLabel: "",
                    gate: data.currentGate || ""
                }));

                const projectsWithTracking = await Promise.all(
                    initialProjects.map(async (project) => {
                        if (project.tracking) {
                            const label = await fetchTrackingLabel(project.tracking);
                            return { ...project, trackingLabel: label };
                        }
                        return project;
                    })
                );

                setProjects(projectsWithTracking);
            } catch (error) {
                console.error("Error fetching projects:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchProjects();
    }, [user.preferred_username]); // rerun when user.name is set


    // Filter projects based on the search term
    const filteredProjects = projects.filter((doc) =>
        doc.title.toLowerCase().includes(searchTerm.toLowerCase())
    );

    // Navigate to project details
    const handleNavigate = (id: string) => {
        setLoading(true)
        router.push(`/project-recap?id=${id}`);
    };

    if (!projects) {
        return <div>
            <Backdrop
                sx={(theme) => ({ color: '#ab3c73', backgroundColor: '#FFF', zIndex: theme.zIndex.drawer + 1 })}
                open={open}
                onClick={handleClose}
            >
                <CircularProgress color="inherit" />
            </Backdrop>
        </div>;
    }

    if (loading) {
        return <div>
            <Backdrop
                sx={(theme) => ({ color: '#ab3c73', backgroundColor: '#FFF', zIndex: theme.zIndex.drawer + 1 })}
                open={open}
                onClick={handleClose}
            >
                <CircularProgress color="inherit" />
            </Backdrop>
        </div>;
    }

    return (
        <div className="pt-12 pb-32 pr-12 pl-12">
            <Link href="/create-project"
                onClick={() => setLoading(true)}
            >
                <Button
                    style={{
                        position: "relative",
                        left: "91%",
                        height: "2rem",
                        backgroundColor: "#ab3c73",
                    }}
                >
                    Add
                </Button>
            </Link>

            {/* Search Bar */}
            <div className="relative right-4 w-2/5">
                <div className="p-4 flex items-center">
                    <input
                        type="text"
                        placeholder="Search for projects..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="bg-white w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-blue-500 text-sm dark:bg-white/[0.05] dark:text-white"
                    />
                    <Image
                        src={"/images/icons/search.png"}
                        alt="search-bar"
                        width={40}
                        height={40}
                        className="w-4 h-4 relative right-8"
                    />
                </div>
            </div>

            <div className="overflow-hidden rounded-xl border border-gray-200 bg-white dark:border-white/[0.05] dark:bg-white/[0.03]">
                {/* Table */}
                <div className="w-full overflow-x-auto">
                    <div className="min-w-[950px] mx-auto">
                        <Table>
                            <TableHeader className="border-b border-gray-100 dark:border-white/[0.05]">
                                <TableRow className="bg-gray-50 dark:bg-white/[0.03]">
                                    {["Title", "Launch date", "Departments", "Tracking", "Actions"].map((head, idx) => (
                                        <TableCell
                                            key={idx}
                                            isHeader
                                            className="px-4 py-2 font-semibold text-gray-600 text-sm text-start dark:text-gray-400"
                                        >
                                            {head}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            </TableHeader>

                            <TableBody className="divide-y divide-gray-100 dark:divide-white/[0.05]">
                                {filteredProjects.map((doc) => (
                                    <TableRow key={doc.id} className="hover:bg-gray-50 dark:hover:bg-white/[0.05]">
                                        {/* Title */}
                                        <TableCell className="px-4 py-4 text-start">
                                            <div className="flex items-center gap-2">
                                                <span className="text-gray-700 text-sm dark:text-white/90">{doc.title}</span>
                                            </div>
                                        </TableCell>

                                        {/* Launch Date */}
                                        <TableCell className="px-4 py-4 text-start text-gray-500 text-sm dark:text-gray-400">
                                            {new Date(doc.launchDate).toDateString()}
                                        </TableCell>

                                        {/* Departments */}
                                        <TableCell className="px-4 py-4 text-start text-gray-500 text-sm dark:text-gray-400">
                                            {doc.departments.join(", ")}
                                        </TableCell>

                                        {/* Tracking - Now showing label instead of ID */}
                                        <TableCell className="px-4 py-4 text-start">
                                            <Badge size="sm" variant="light" className="text-gray-600 bg-gray-100 dark:bg-white/[0.05] dark:text-white/70">
                                                {doc.trackingLabel.toLowerCase() || "No Tracking"}
                                            </Badge>
                                        </TableCell>

                                        {/* Actions */}
                                        <TableCell className="px-4 py-4 text-start">
                                            <Image
                                                style={{
                                                    cursor: "pointer",
                                                }}
                                                src="/images/icons/details/fichier.png"
                                                onClick={() => handleNavigate(doc.id)}
                                                title="project details"
                                                alt="project details"
                                                width={900}
                                                height={900}
                                                className="w-7 h-7"
                                            />
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </div>
                </div>
            </div>
        </div>
    );
}