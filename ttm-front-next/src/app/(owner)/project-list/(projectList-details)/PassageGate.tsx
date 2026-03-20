import TextArea from "@/components/form/input/TextArea";
import Image from "next/image";
import Link from "next/link";
import { useEffect, useState } from "react";

import { getCurrentGateIdByProjectId, getNextGateByProjectProjectId, moveToNextGate, UpdateCurrentGateProject } from "@/axios/ProjectApis";
import Button from "../../../../components/ui/button/Button";

interface ProjectDetails {
    id: number;
    owner: string;
    title: string;
    description: string;
    launchDate: Date;
    passageDate: Date;
    departments: string[];
    ownerMoa: string[];
    tracking: string;
    gate: string;
}

interface Alert {
    type: 'success' | 'danger';
    message: string;
    show: boolean;
}


export default function PassageGate({ projectDetails }: { projectDetails: ProjectDetails }) {
    const [infoMessage, setInfoMessage] = useState("");
    const [actionsMessage, setActionsMessage] = useState("");
    const [decisionsMessage, setDecisionsMessage] = useState("");

    const [projectIsDone, setProjectIsDone] = useState(false);

    const [nextGate, setNextGate] = useState("");
    const [disabelButton, setDisableButton] = useState(false);
    const [selectedDecision, setSelectedDecision] = useState("");
    const [showConfirmModal, setShowConfirmModal] = useState(false);
    const [currentGateId, setCurrentGateId] = useState(null);
    const [alert, setAlert] = useState<Alert>({ type: 'success', message: '', show: false });

    const today = new Date();
    const formattedDate = today.toLocaleDateString('fr-FR');

    const gateOptions = [
        { label: "T_2" },
        { label: "T_2Bis" },
        { label: "T_1" },
        { label: "T_1Bis" },
        { label: "T_1Ter" },
        { label: "T0" },
        { label: "T0Bis" },
        { label: "T0Ter" },
        { label: "T1" },
        { label: "T2" },
        { label: "T3" },
        { label: "T3Bis" },
        { label: "T3Ter" },
        { label: "T4" },
        { label: "T5" },
        { label: "T6" },
    ];


    // Function to show alerts
    const showAlert = (type: 'success' | 'danger', message: string) => {
        setAlert({ type, message, show: true });

        // Auto-hide alert after 4 seconds
        setTimeout(() => {
            setAlert(prev => ({ ...prev, show: false }));
        }, 5000);
    };

    // Function to hide alert manually
    const hideAlert = () => {
        setAlert(prev => ({ ...prev, show: false }));
    };






    {/* GET CURRENT GATE*/ }
    useEffect(() => {
        const getCurrentGateAndCheckStatus = async () => {
            if (!projectDetails?.id) {
                console.warn("[getCurrentGateId] No project ID available");
                return;
            }

            try {
                console.log("Making API call to getCurrentGateIdByProjectId");
                const response = await getCurrentGateIdByProjectId(Number(projectDetails.id));
                const { message, status } = response.data;

                console.log("Current Gate ID response:", message, status);

                if (response.data !== null && response.data !== undefined) {
                    const fetchedGateId = response.data;
                    setCurrentGateId(fetchedGateId);

                    // Call checkProjectStatus after setting gate ID
                    await checkProjectStatus(fetchedGateId);

                } else if (message?.includes('No current gate found')) {
                    console.warn("Project passed all gates");
                    setCurrentGateId(null);
                    showAlert('success', 'Congrats! this project has been completed.');
                }
            } catch (error) {
                console.error("Error fetching current gate ID:", error);
                if (error && typeof error === 'object' && 'response' in error) {
                    const axiosError = error as { response: { status: number; data: unknown; statusText: string } };
                    console.error("[getCurrentGateId] Error Status:", axiosError.response.status);
                    console.error("[getCurrentGateId] Error Status Text:", axiosError.response.statusText);
                    console.error("[getCurrentGateId] Error Data:", axiosError.response.data);
                }

                setCurrentGateId(null);
                showAlert('danger', 'Error loading gate information. Please try again.');
            }

            console.log("[getCurrentGateId] Function completed");
        };

        const checkProjectStatus = async (gateId: number) => {

            const data = {
                id: gateId,
                gate: projectDetails.gate,
                decisionType: "GO",
                information: infoMessage ? [infoMessage] : [],
                actions: actionsMessage ? [actionsMessage] : [],
                decisions: decisionsMessage ? [decisionsMessage] : [],
                projectId: projectDetails.id
            };

            console.log("Data to send to API:", data);

            try {
                const response = await UpdateCurrentGateProject(gateId, data);
                const { projectCompleted } = response.data;

                console.log("Response from API:", response.data);
                console.log("projectCompleted:", projectCompleted);

                if (projectCompleted) {
                    setProjectIsDone(true);
                    setDisableButton(true);
                    showAlert('success', 'Congrats!! The project has been completed.');
                }
            } catch (error) {
                console.error("Error updating current gate project:", error);

            }
        };

        getCurrentGateAndCheckStatus();

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [projectDetails?.id]);


    {/* GET NEXT GATE*/ }
    useEffect(() => {
        const fetchNextGate = async () => {
            if (!projectDetails?.id) return;

            console.log("Fetching Next Gate for project:", projectDetails?.id);

            try {
                const response = await getNextGateByProjectProjectId(Number(projectDetails.id));

                const { status } = response.data;

                if (status === 500) {
                    setNextGate("");
                    setProjectIsDone(true)
                } else if (status === 400) {
                    setNextGate("");
                } else {
                    setNextGate(response.data);
                }

            } catch (error) {
                console.error("Unhandled error:", error);
                showAlert('danger', `Unhandled error: ${error}`)
            }

        };


        fetchNextGate();
    }, [projectDetails?.id]);






    // Enhanced logging for current state
    console.log("Current Gate ID from state:", currentGateId);



    {/* Handle Submit and receive update-response-gate value */ }
    const handleSubmit = async () => {
        if (currentGateId === null) {
            console.error("Current Gate ID is not set.");
            showAlert('danger', 'Current Gate ID is not set. Please refresh and try again.');
            return;
        }

        if (!selectedDecision) {
            showAlert('danger', 'Please select a decision before submitting.');
            return;
        }

        const data = {
            id: currentGateId,
            gate: projectDetails.gate,
            decisionType: selectedDecision.toUpperCase(),
            information: infoMessage ? [infoMessage] : [],
            actions: actionsMessage ? [actionsMessage] : [],
            decisions: decisionsMessage ? [decisionsMessage] : [],
            projectId: projectDetails.id
        };

        console.log("Data to send to API:", data);

        try {
            const response = await UpdateCurrentGateProject(currentGateId, data);

            const { projectCompleted } = response.data
            console.log("Response from API:", response.data);
            console.log("projectCompleted:", projectCompleted);

            if (projectCompleted) {
                setProjectIsDone(true)
                setDisableButton(true)
                showAlert('success', 'Congrats!! The project has been completed.');

                setTimeout(() => {
                    window.location.reload();
                }, 5000);

                return;

            }
            showAlert('success', 'Gate information updated successfully!');
            setShowConfirmModal(true);

            setTimeout(() => {
                window.location.reload();
            }, 5000);


        } catch (error) {
            console.error("Error updating current gate project:", error);
            showAlert('danger', 'Failed to update gate information. Please try again.');
        }
    };

    const handleConfirmSubmit = async () => {
        if (projectDetails.id) {
            try {
                const response = await moveToNextGate(projectDetails.id);
                console.log("Response from API:", response.data);
                showAlert('success', 'Successfully moved to next gate!');
            } catch (error) {
                console.error("Error updating current gate project:", error);
                showAlert('danger', 'Failed to move to next gate. Please try again.');
            }
        }

        setShowConfirmModal(false);
    };

    const cancelSubmit = () => {
        setShowConfirmModal(false);
    };

    console.log("Selected Decision:", selectedDecision);

    return (
        <div className="pb-20">

            {/* Alert Component */}
            {alert.show && (
                <div className={`fixed top-18 right-20 z-50 max-w-md w-full mx-4 rounded-lg shadow-lg border-l-4 transition-all duration-1000 transform ${alert.show ? 'translate-x-0 opacity-100' : 'translate-x-full opacity-0'
                    } ${alert.type === 'success'
                        ? 'bg-green-50 border-green-400 text-green-800'
                        : 'bg-red-50 border-red-400 text-red-800'
                    }`}>
                    <div className="flex items-center justify-between p-4">
                        <div className="flex items-center">
                            <div className={`flex-shrink-0 w-5 h-5 mr-3 ${alert.type === 'success' ? 'text-green-400' : 'text-red-400'
                                }`}>
                                {alert.type === 'success' ? (
                                    <svg fill="currentColor" viewBox="0 0 20 20">
                                        <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                                    </svg>
                                ) : (
                                    <svg fill="currentColor" viewBox="0 0 20 20">
                                        <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
                                    </svg>
                                )}
                            </div>
                            <div className="flex-1">
                                <p className="text-sm font-medium">{alert.message}</p>
                            </div>
                        </div>
                        <button
                            onClick={hideAlert}
                            className={`ml-4 text-sm font-medium ${alert.type === 'success'
                                ? 'text-green-600 hover:text-green-500'
                                : 'text-red-600 hover:text-red-500'
                                } focus:outline-none`}
                        >
                            ✕
                        </button>
                    </div>
                </div>
            )}

            {/* Confirmation Modal */}
            {showConfirmModal && (
                <div className="fixed inset-0 backdrop-blur-lg bg-opacity-10 flex items-center justify-center z-50">
                    <div className="bg-white rounded-lg p-8 max-w-md w-full mx-4 shadow-2xl border border-gray-200">
                        <div className="flex items-center mb-4">
                            <Image
                                src="/images/icons/actions/warning-.png"
                                alt="Warning"
                                width={24}
                                height={24}
                                className="mr-3"
                            />
                            <h2 className="text-lg font-bold text-gray-800">Confirm Submission</h2>
                        </div>

                        <p className="text-gray-600 mb-6 w-fit">
                            Are you sure you want to submit this passage gate?
                        </p>

                        <div className="flex justify-end gap-4">
                            <Button
                                onClick={cancelSubmit}
                                className="h-10 bg-gray-200 text-gray-800 px-6 py-2 rounded-full hover:bg-gray-300 border-none"
                                style={{ color: "#000" }}
                            >
                                Cancel
                            </Button>
                            <Button
                                onClick={handleConfirmSubmit}
                                className="h-10 px-6 py-2 rounded-full border-none"
                                style={{ backgroundColor: "#ab3c73" }}
                            >
                                Confirm
                            </Button>
                        </div>
                    </div>
                </div>
            )}

            <div className="bg-[#ecf2f5]">
                <div className="bg-[#FFF] p-8 flex flex-nowrap justify-between items-center content-center ">
                    <div className="flex flex-nowrap justify-start items-center content-center gap-8">

                        {/* Prochain Gate */}
                        <div className="flex flex-nowrap items-center content-center gap-2">
                            <h1 className="text-sm font-bold text-nowrap content-center items-center" > Gate :</h1>
                            <div className="text-sm border border-[#d9d9d9] px-3 py-[2px] rounded-full bg-[#f9f9f9] text-center cursor-pointer">
                                <p className=" text-nowrap content-center items-center" > {projectIsDone ? "Project completed" : projectDetails.gate}  </p>
                            </div>
                        </div>

                        <div className="h-8 w-0.25 bg-[#e8e8e8]"></div>

                        {/* Date de passage */}
                        <div className="flex flex-nowrap items-center content-center gap-2">
                            <h1 className="text-sm font-bold text-nowrap content-center items-center"> Passage date :</h1>
                            <div className="text-sm border border-[#d9d9d9] px-3 py-1 rounded-full bg-[#f9f9f9] text-center cursor-pointer">
                                <p className="text-nowrap content-center items-center" >{projectDetails.passageDate.toLocaleString("fr-FR", { dateStyle: "short", timeStyle: "short" })}</p>
                            </div>
                        </div>
                    </div>

                    <div className="flex  justify-end items-center content-center gap-4">
                        {/* Cancel Button */}
                        <Link href="/project-list">
                            <Button
                                className="bg-white w-[6.5rem] h-[1.5rem] border-none rounded-full hover:bg-gray-200"
                                style={{ color: "#000" }}>
                                Cancel
                            </Button>
                        </Link>

                        {/* Submit Button - Update Gate */}
                        <Button
                            onClick={handleSubmit}
                            className="w-[6.5rem] h-[1.5rem] border-none rounded-full"
                            style={{ backgroundColor: "#ab3c73" }}
                            disabled={disabelButton ? true : false}
                        >
                            Submit
                        </Button>
                    </div>
                </div>

                <div className="flex justify-items-start gap-7 items-center content-center py-8 px-8">
                    <p style={{ fontSize: "14.5px", fontWeight: "bold" }}
                        className="text-nowrap text-"
                    >Decision : </p>

                    <div className="pl-12 flex justify-evenly content-center gap-3">
                        {[
                            { label: "Go", icon: "/images/icons/actions/valide.png" },
                            { label: "No Go", icon: "/images/icons/actions/proche.png" },
                            { label: "Go with modifications", icon: "/images/icons/actions/wr-bl.png" },
                            { label: "Go with reserve", icon: "/images/icons/actions/warning-.png" },
                        ].map(({ label, icon }) => (
                            <div
                                key={label}
                                onClick={() => setSelectedDecision(label)}
                                className={`text-xs border px-3 py-1 rounded-full text-center cursor-pointer flex items-center justify-center gap-2 transition-all
                                        ${selectedDecision === label
                                        ? "border-[#6756ea] border-[1.5px] bg-[#f9f9f9] text-gray-700"
                                        : "border-[#d9d9d9] bg-[#f9f9f9] text-gray-700"
                                    }`}
                            >
                                <Image src={icon} alt={label} className="w-4 h-4" width={16} height={16} />
                                <p className="text-nowrap">{label}</p>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="w-full flex justify-items-start gap-7 items-start content-center py-2 px-8">
                    <p style={{ fontSize: "14.5px", fontWeight: "bold" }}
                        className="text-nowrap"
                    >Informations : </p>

                    <div className="w-[86%] pl-6">
                        <TextArea
                            className=" text-sm text-gray-800"
                            style={{ backgroundColor: "#FFF" }}
                            value={infoMessage}
                            onChange={(e) => setInfoMessage(e.target.value)}
                            rows={6}
                            placeholder="Ajouter des informations sur le projet.."
                        />
                    </div>
                </div>

                <div className="w-full flex justify-items-start gap-16 items-start content-center py-1 px-8">
                    <p style={{ fontSize: "14.5px", fontWeight: "bold" }}
                        className="text-nowrap"
                    >Actions : </p>

                    <div className="w-[86%] pl-6">
                        <TextArea
                            className=" text-sm text-gray-800"
                            style={{ backgroundColor: "#FFF" }}
                            value={actionsMessage}
                            onChange={(e) => setActionsMessage(e.target.value)} rows={6}
                            placeholder="Insérer vos actions.."
                        />
                    </div>
                </div>

                <div className="w-full flex justify-items-start gap-14 items-start content-center py-1 px-8">
                    <p style={{ fontSize: "13.5px", fontWeight: "bold" }}
                        className="text-nowrap"
                    >Decisions : </p>

                    <div className="w-[86%] pl-6">
                        <TextArea
                            className=" text-sm text-gray-800"
                            style={{ backgroundColor: "#FFF" }}
                            value={decisionsMessage}
                            onChange={(e) => setDecisionsMessage(e.target.value)}
                            rows={6}
                            placeholder="Ajouter un décision.. "
                        />
                    </div>
                </div>

                <div className="flex justify-items-start gap-6 items-center content-center  py-4 px-8">
                    <p style={{ fontSize: "14.5px" }}
                        className="text-nowrap font-bold "
                    >
                        Next Gate :
                    </p>

                    <div className="pl-12 flex flex-wrap justify-start content-center gap-1">
                        {gateOptions.map((option, index) => (
                            <div
                                key={index}
                                className={`text-xs border px-3 py-[2px] rounded-full text-center cursor-default
        ${option.label === nextGate
                                        ? "border-[#6756ea] bg-[#fdf8f8] border-[1.5px] text-gray-600 font-semibold"
                                        : "border-[#d9d9d9] bg-white text-gray-400 opacity-90"
                                    }`}
                            >
                                <p className="text-nowrap">{option.label}</p>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="p-8 flex items-center content-center">
                    {/* Date de passage */}
                    <div className="text-nowrap pr-8 flex items-center content-center gap-4.5">
                        <h1 className="text-nowrap font-bold text-sm">Passage Date :</h1>
                        <div className="text-sm border border-[#d9d9d9] px-3 py-1 rounded-[10px] bg-[#f9f9f9] text-center items-center justify-center cursor-pointer flex gap-[15px]">
                            <p className="text-nowrap items-center content-center" >{formattedDate}</p>
                            <Image src="/images/icons/date/date.png" alt="Kickoff marché icon" width={20} height={20} />
                        </div>
                    </div>

                    <div className="h-8 w-0.25" style={{ backgroundColor: "#e8e8e8" }}></div>

                    {/* Date de lancement TTM */}
                    <div className="text-nowrap px-8 pl-8 flex items-center content-center gap-4.5">
                        <h1 className="text-nowrap font-bold text-sm"> Date T0 :</h1>

                        <div className="text-sm border border-[#d9d9d9] px-3 py-1 rounded-[10px] bg-[#f9f9f9] text-center items-center justify-center cursor-pointer flex gap-[15px]">
                            <p className="text-nowrap items-center content-center">{projectDetails.launchDate.toLocaleDateString("fr-FR")}</p>
                            <Image src="/images/icons/date/date.png"
                                alt="Kickoff marché icon"
                                width={20} height={20} />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}