import DropzoneComponent from "@/components/form/form-elements/DropZone";
import Image from "next/image";
import React from "react";

export default function RequiredActions() {
    const actions = [
        {
            label: "Retour d'impact",
            title: "Retour Process B2C",
            options: [
                { label: "Accepter", icon: "/images/icons/actions/valide.png" },
                { label: "À modifier", icon: "/images/icons/actions/warning-.png" },
                { label: "Refuser", icon: "/images/icons/actions/proche.png" },
            ]

        },
        {
            label: "Retour d'impact",
            title: "Retour Service client B2C",
            options: [
                { label: "Accepter", icon: "/images/icons/actions/valide.png" },
                { label: "À modifier", icon: "/images/icons/actions/warning-.png" },
                { label: "Refuser", icon: "/images/icons/actions/proche.png" },
            ]


        },
        {
            label: "Retour d'impact",
            title: "Retour Analyse business B2C",
            options: [
                { label: "Accepter", icon: "/images/icons/actions/valide.png" },
                { label: "À modifier", icon: "/images/icons/actions/warning-.png" },
                { label: "Refuser", icon: "/images/icons/actions/proche.png" },
            ]
        },
        {
            label: "Implication sur les systémes comptables",
            title: "Retour Facturation B2C",
            options: [
                { label: "Accepter", icon: "/images/icons/actions/valide.png" },
                { label: "À modifier", icon: "/images/icons/actions/warning-.png" },
                { label: "Refuser", icon: "/images/icons/actions/proche.png" },
            ]
        },
        {
            label: "Retour d'impact",
            title: "Retour Comptabilité B2C",
            options: [
                { label: "Accepter", icon: "/images/icons/actions/valide.png" },
                { label: "À modifier", icon: "/images/icons/actions/warning-.png" },
                { label: "Refuser", icon: "/images/icons/actions/proche.png" },
            ]
        },
        {
            label: "Premier chiffrage",
            title: "Retour Comptabilité B2C",
            options: [
                { label: "Accepter", icon: "/images/icons/actions/valide.png" },
                { label: "À modifier", icon: "/images/icons/actions/warning-.png" },
                { label: "Refuser", icon: "/images/icons/actions/proche.png" },
            ]
        },
        {
            label: "Retour d'impact",
            title: "Retour Comptabilité B2C",
            options: [
                { label: "Accepter", icon: "/images/icons/actions/valide.png" },
                { label: "À modifier", icon: "/images/icons/actions/warning-.png" },
                { label: "Refuser", icon: "/images/icons/actions/proche.png" },
            ]
        },
        {
            label: "Analyse de risque",
            title: "Retour Comptabilité B2C",
            options: [
                { label: "Accepter", icon: "/images/icons/actions/valide.png" },
                { label: "À modifier", icon: "/images/icons/actions/warning-.png" },
                { label: "Refuser", icon: "/images/icons/actions/proche.png" },
            ]
        },
        {
            label: "Retour d'impact",
            title: "Retour Comptabilité B2C",
            options: [
                { label: "Accepter", icon: "/images/icons/actions/valide.png" },
                { label: "À modifier", icon: "/images/icons/actions/warning-.png" },
                { label: "Refuser", icon: "/images/icons/actions/proche.png" },
            ]
        },
        {
            label: "Retour d'impact",
            title: "Retour Comptabilité B2C",
            options: [
                { label: "Accepter", icon: "/images/icons/actions/valide.png" },
                { label: "À modifier", icon: "/images/icons/actions/warning-.png" },
                { label: "Refuser", icon: "/images/icons/actions/proche.png" },
            ]
        },
    ];



    const boxStyle: React.CSSProperties = {
        fontSize: "12px",
        border: "1px solid #d9d9d9",
        padding: "2px 12px",
        borderRadius: "20px",
        backgroundColor: "#f9f9f9",
        textAlign: "center",
        width: "140px",
        display: "flex",
        alignItems: "center",
    };

    const entityStyleRightSide: React.CSSProperties = {
        fontSize: "12px",
        border: "1px solid #d9d9d9",
        padding: "2px 12px",
        borderRadius: "20px",
        backgroundColor: "#f9f9f9",
        textAlign: "center",
        marginLeft: "3rem",
        position: "absolute",
        left: "27rem",

    };

    const entityStyleLeftSide: React.CSSProperties = {
        fontSize: "12px",
        border: "1px solid #d9d9d9",
        padding: "2px 12px",
        borderRadius: "20px",
        backgroundColor: "#f9f9f9",
        textAlign: "center",
        marginLeft: "3rem",
        position: "absolute",
        left: "61rem",

    };



    return (
        <div style={{ fontFamily: "Arial, sans-serif" }}>

            {/* Header Section */}
            <div style={{ paddingTop: "1rem", paddingBottom: "1rem" }}>

                <label htmlFor="assignee" style={{ fontWeight: "bold" }}>
                    Affecter le sujet à
                </label>

                <div style={{
                    backgroundColor: "#FFF",
                    position: "relative",
                    top: "9px",
                    paddingTop: "1rem",
                    paddingBottom: "1rem",
                    paddingLeft: "2rem",
                    borderRadius: "8px",
                    border: "1px solid #ddd",
                    fontSize: "15px",
                    color: "#000",
                }}>


                    <Image
                        src="/images/icons/users/user.png"
                        alt="project description icon"
                        width={100}
                        height={100}
                        className="w-7 h-7"
                        style={{
                            position: "absolute",
                            top: "0.8rem",
                            left: "1.5rem",

                        }}
                    />

                    <p style={{
                        paddingLeft: "2rem",
                        fontWeight: "bold",
                        color: "#444444",
                    }}>
                        Oussama Bakkari
                    </p>

                </div>

            </div>


            {/* Main Content Section */}
            <div
                style={{
                    display: "flex",
                    gap: "20px",
                    paddingTop: "1rem",
                }}
            >
                {/* Left Column */}
                <div style={{ flex: 1 }}>
                    <>
                        {actions.slice(0, Math.ceil(actions.length / 2)).map((action, index) => (
                            <div key={index} style={{ marginBottom: "15px", paddingBottom: "1rem" }}>

                                {/* Label */}
                                <label
                                    style={{
                                        display: "block",
                                        fontWeight: "bold",
                                        marginBottom: "5px",
                                        fontSize: "14px",
                                        color: "#333",
                                        paddingBottom: "3px",
                                    }}
                                >
                                    {action.label}
                                </label>

                                {/* Styled div */}
                                <div
                                    style={{
                                        border: "1px solid #ddd",
                                        borderRadius: "8px",
                                        padding: "15px",
                                        boxShadow: "0 2px 5px rgba(0, 0, 0, 0.1)",
                                        backgroundColor: "#FFFF",
                                        paddingBottom: "1.5rem",
                                    }}
                                >
                                    <div
                                        style={{
                                            display: "flex",
                                            padding: "30px",
                                            alignItems: "center",
                                            justifyContent: "space-between",
                                            marginBottom: "10px",
                                            backgroundColor: "#f9f9f9",
                                            borderRadius: "10px",
                                            border: "1px solid #ddd",
                                            height: "70px",
                                        }}
                                    >
                                        <span
                                            style={{
                                                textDecoration: "underline",
                                                textUnderlineOffset: "2px",
                                                fontSize: "13px",
                                                color: "#2c4f95",
                                            }}
                                        >
                                            {action.title}
                                        </span>

                                        <button style={entityStyleRightSide}>Entité</button>


                                        <Image
                                            src="/images/icons/download-icons/v1.png"
                                            alt="downlad icon"
                                            className="w-6 h-6"
                                            width={12} // Icon width
                                            height={12} // Icon height
                                        />

                                    </div>


                                    <div
                                        style={{
                                            display: "flex",
                                            justifyContent: "space-between",
                                            paddingLeft: "10px",
                                            paddingRight: "10px",
                                            paddingTop: "10px",
                                        }}
                                    >
                                        {action.options.map((option, idx) => (
                                            <div key={idx} style={boxStyle}>
                                                <div>
                                                    <Image
                                                        style={{
                                                            position: "relative",
                                                            marginRight: "27px",
                                                            left: "22px"
                                                        }}

                                                        src={option.icon}
                                                        alt={option.label}
                                                        width={12} // Icon width
                                                        height={12} // Icon height
                                                    />
                                                </div>


                                                <button key={idx} >
                                                    {option.label}
                                                </button>
                                            </div>

                                        ))}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </>
                </div>

                {/* Right Column */}
                <div style={{ flex: 1 }}>
                    <>
                        {actions.slice(Math.ceil(actions.length / 2)).map((action, index) => (
                            <div key={index} style={{ marginBottom: "15px", paddingBottom: "1rem" }}>

                                {/* Label */}
                                <label
                                    style={{
                                        display: "block",
                                        fontWeight: "bold",
                                        marginBottom: "5px",
                                        fontSize: "14px",
                                        color: "#333",
                                        paddingBottom: "3px",
                                    }}
                                >
                                    {action.label}
                                </label>

                                {/* Styled div */}
                                <div
                                    style={{
                                        border: "1px solid #ddd",
                                        borderRadius: "8px",
                                        padding: "15px",
                                        boxShadow: "0 2px 5px rgba(0, 0, 0, 0.1)",
                                        backgroundColor: "#FFFF",
                                        paddingBottom: "1.5rem",
                                    }}
                                >
                                    <div
                                        style={{
                                            display: "flex",
                                            padding: "30px",
                                            justifyContent: "space-between",
                                            alignItems: "center",
                                            marginBottom: "10px",
                                            backgroundColor: "#f9f9f9",
                                            borderRadius: "10px",
                                            border: "1px solid #ddd",
                                            height: "70px",
                                        }}
                                    >
                                        <span
                                            style={{
                                                textDecoration: "underline",
                                                textUnderlineOffset: "2px",
                                                fontSize: "13px",
                                                color: "#2c4f95",
                                            }}
                                        >
                                            {action.title}
                                        </span>
                                        <button style={entityStyleLeftSide}>Entité</button>


                                        <Image
                                            src="/images/icons/download-icons/v1.png"
                                            alt="downlad icon"
                                            className="w-6 h-6"
                                            width={12} // Icon width
                                            height={12} // Icon height
                                        />

                                    </div>
                                    <div
                                        style={{
                                            display: "flex",
                                            justifyContent: "space-between",
                                            paddingLeft: "10px",
                                            paddingRight: "10px",
                                            paddingTop: "10px",
                                        }}
                                    >
                                        {action.options.map((option, idx) => (

                                            <div key={idx} style={boxStyle}>

                                                <div>
                                                    <Image

                                                        style={{
                                                            position: "relative",
                                                            marginRight: "27px",
                                                            left: "22px"


                                                        }}
                                                        src={option.icon}
                                                        alt={option.label}
                                                        width={12} // Icon width
                                                        height={12} // Icon height
                                                    />
                                                </div>


                                                <button key={idx} >
                                                    {option.label}
                                                </button>
                                            </div>

                                        ))}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </>
                </div>
            </div>

            {/* The DropZone */}
            <div
                style={{
                    position: "relative",
                    paddingTop: "1rem",
                    paddingBottom: "3rem",

                }}>
                <DropzoneComponent label="Compléments (optionnel)" />

            </div>
        </div>
    );
}