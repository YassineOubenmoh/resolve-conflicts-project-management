/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { affectGatesToDepartment, getDepartments, getGatesResponseByProjectId } from '@/axios/ProjectApis';
import { Check, ChevronDown, ChevronUp, Settings } from 'lucide-react';
import { useEffect, useState } from 'react';

// Types
interface Department {
    id: number;
    department: string;
}

interface Gate {
    id: number;
    name: string;
    description?: string;
}

interface DepartmentGateAssignment {
    departmentId: number;
    gateIds: number[];
}

const AffectGatesToDepartements = () => {
    const [departments, setDepartments] = useState<Department[]>([]);
    const [gates, setGates] = useState<Gate[]>([]);
    const [expandedDepartment, setExpandedDepartment] = useState<number | null>(null);
    const [gateAssignments, setGateAssignments] = useState<Record<number, number[]>>({});
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);

    // Fetch departments from API using axios
    useEffect(() => {
        const fetchDepartmentsAndGates = async () => {
            try {
                // Fetch departments
                const rawDepartments = await getDepartments();
                const cleanedDepartments: Department[] = rawDepartments.map((d: any) => ({
                    id: d.id,
                    department: d.departement
                }));

                setDepartments(cleanedDepartments);

                // Fetch gates
                const projectId = localStorage.getItem('createdProjectId');
                if (projectId === null) {
                    console.error('Project ID is not set in local storage');
                    return;
                }

                console.log("projectId: ", projectId)
                const rawGates = await getGatesResponseByProjectId(parseInt(projectId, 10));

                console.log("rawGates data: ", rawGates.data)

                const simplifiedGates: Gate[] = rawGates.data.map((g: any) => ({
                    id: g.id,
                    name: g.gate
                }));
                setGates(simplifiedGates);

            } catch (error) {
                console.error('Error fetching data:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchDepartmentsAndGates();
    }, []);

    const toggleDepartmentExpansion = (departmentId: number) => {
        setExpandedDepartment(expandedDepartment === departmentId ? null : departmentId);
    };

    const handleGateSelection = (departmentId: number, gateId: number) => {
        setGateAssignments(prev => {
            const currentGates = prev[departmentId] || [];
            const isSelected = currentGates.includes(gateId);

            return {
                ...prev,
                [departmentId]: isSelected
                    ? currentGates.filter(id => id !== gateId)
                    : [...currentGates, gateId]
            };
        });
    };

    const handleSaveAssignments = async () => {
        setSaving(true);
        try {
            // Convert gateAssignments to the format expected by your API
            const assignments: DepartmentGateAssignment[] = Object.entries(gateAssignments)
                .map(([departmentId, gateIds]) => ({
                    departmentId: parseInt(departmentId),
                    gateIds
                }))
                .filter(assignment => assignment.gateIds.length > 0);

            console.log('Saving gate assignments:', assignments);

            // Make API calls for each department
            const apiCalls = assignments.map(async (assignment) => {
                try {
                    const response = await affectGatesToDepartment(assignment.departmentId, assignment.gateIds)

                    console.log(`Successfully assigned gates to department ${assignment.departmentId}:`, response.data);
                    return { success: true, departmentId: assignment.departmentId };
                } catch (error) {
                    console.error(`Error assigning gates to department ${assignment.departmentId}:`, error);
                    return { success: false, departmentId: assignment.departmentId, error };
                }
            });

            // Wait for all API calls to complete
            const results = await Promise.all(apiCalls);

            // Check if all calls were successful
            const failedCalls = results.filter(result => !result.success);

            if (failedCalls.length === 0) {
                alert('Gate assignments saved successfully!');


            } else {
                const failedDepartments = failedCalls.map(call => call.departmentId).join(', ');
                alert(`Some assignments failed for departments: ${failedDepartments}. Please try again.`);
            }

        } catch (error) {
            console.error('Error saving gate assignments:', error);
            alert('Error saving gate assignments. Please try again.');
        } finally {
            setSaving(false);
        }
    };

    const redirectionButton = () => {
        // Redirect to project list or dashboard
        setTimeout(() => {
            window.location.href = "/project-list";
        }, 1000);
    }

    const getSelectedGatesCount = (departmentId: number) => {
        return gateAssignments[departmentId]?.length || 0;
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#ab3c73] mx-auto"></div>
                    <p className="mt-4 text-gray-600">Loading departments...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 py-20 px-4 sm:px-6 lg:px-8">
            <div className="max-w-4xl mx-auto">
                {/* Success Message Card */}
                <div className="bg-white rounded-xl shadow-lg p-6 mb-8 border-l-4 border-green-500">
                    <div className="flex items-center">
                        <div className="bg-green-100 rounded-full p-3 mr-4">
                            <Check className="h-6 w-6 text-green-600" />
                        </div>
                        <div>
                            <h1 className="text-xl font-bold text-gray-900 mb-2">
                                Project Created Successfully!
                            </h1>
                            <p className="text-gray-600 text-l">
                                Your project has been created successfully. Please assign gates to departments below.
                            </p>
                        </div>
                    </div>
                </div>

                {/* Department Gates Assignment */}
                <div className="bg-white rounded-xl shadow-lg p-6">
                    <div className="flex justify-between items-center mb-6">
                        <h2 className="text-lg font-semibold text-gray-800">
                            Assign Gates to Departments
                        </h2>
                        <button
                            onClick={redirectionButton}
                            disabled={saving || Object.keys(gateAssignments).length === 0}
                            className="bg-[#ad0377] hover:bg-[#ab3c73] disabled:bg-gray-400 disabled:cursor-not-allowed text-white px-6 py-2 rounded-lg font-medium transition-colors duration-200 flex items-center gap-2"
                        >

                            Done
                        </button>
                    </div>

                    <div className="space-y-4">
                        {departments.map((department) => (
                            <div
                                key={department.id}
                                className="border border-gray-200 rounded-lg overflow-hidden"
                            >
                                {/* Department Header */}
                                <div className="bg-gray-50 p-4 flex items-center justify-between">
                                    <div className="flex items-center gap-3">
                                        <h3 className="font-medium text-gray-900">{department.department.toUpperCase()}</h3>
                                        {getSelectedGatesCount(department.id) > 0 && (
                                            <span className="bg-purple-100 text-purple-800 text-xs font-medium px-2.5 py-0.5 rounded-full">
                                                {getSelectedGatesCount(department.id)} gate{getSelectedGatesCount(department.id) !== 1 ? 's' : ''} selected
                                            </span>
                                        )}
                                    </div>
                                    <button
                                        onClick={() => toggleDepartmentExpansion(department.id)}
                                        className="flex items-center gap-2 text-[#590893] hover:text-[#330058] transition-colors duration-200"
                                    >
                                        <Settings className="h-4 w-4" />
                                        <span className="text-sm font-medium">Assign Gates</span>
                                        {expandedDepartment === department.id ? (
                                            <ChevronUp className="h-4 w-4" />
                                        ) : (
                                            <ChevronDown className="h-4 w-4" />
                                        )}
                                    </button>
                                </div>

                                {/* Gates Selection Panel */}
                                {expandedDepartment === department.id && (
                                    <div className="p-4 bg-white border-t border-gray-200">
                                        <p className="text-sm text-gray-600 mb-4">
                                            Select the gates that this department should be involved in:
                                        </p>
                                        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                            {gates.map((gate) => (
                                                <label
                                                    key={gate.id}
                                                    className="flex items-start gap-3 p-3 border border-gray-200 rounded-lg hover:bg-gray-50 cursor-pointer transition-colors duration-200"
                                                >
                                                    <input
                                                        type="checkbox"
                                                        checked={gateAssignments[department.id]?.includes(gate.id) || false}
                                                        onChange={() => handleGateSelection(department.id, gate.id)}
                                                        className="mt-1 h-4 w-4 rounded border border-gray-300 accent-[#d03aa0]"
                                                    />
                                                    <div className="flex-1">
                                                        <div className="font-medium text-gray-900 text-sm">
                                                            {gate.name}
                                                        </div>
                                                        {gate.description && (
                                                            <div className="text-xs text-gray-500 mt-1">
                                                                {gate.description}
                                                            </div>
                                                        )}
                                                    </div>
                                                </label>
                                            ))}
                                        </div>


                                        <button
                                            onClick={handleSaveAssignments}
                                            disabled={saving || Object.keys(gateAssignments).length === 0}
                                            className="bg-[#ad0377] hover:bg-[#ab3c73] disabled:bg-gray-400 disabled:cursor-not-allowed text-white px-6 py-2 mt-3 relative rounded-lg font-medium transition-colors duration-200 "
                                        >
                                            {saving ? (
                                                <>
                                                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                                                    Saving...
                                                </>
                                            ) : (
                                                'Save Assignments'
                                            )}
                                        </button>



                                    </div>



                                )}
                            </div>
                        ))}
                    </div>

                    {departments.length === 0 && (
                        <div className="text-center py-8">
                            <p className="text-gray-500">No departments found.</p>
                        </div>
                    )}
                </div>

                {/* Skip Option */}
                <div className="text-center mt-6">
                    <button
                        onClick={() => window.location.href = "/project-list"}
                        className="text-gray-500 hover:text-gray-700 text-sm underline"
                    >
                        Skip for now and go to project list
                    </button>
                </div>
            </div>
        </div>
    );
};

export default AffectGatesToDepartements;