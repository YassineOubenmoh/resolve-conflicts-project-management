import { getNextGateByProjectProjectId, getTrackingById } from "@/axios/ProjectApis";
import ComponentCard from "@/components/common/ComponentCard";
import Image from "next/image";
import { useEffect, useState } from "react";

interface Project {
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


export default function ViewGlobal({ projectDetails }: { projectDetails: Project }) {


  const [trackingLabel, setTrackingLabel] = useState("");
  const [nextGate, setNextGate] = useState("");
  const [nextGateError, setNextGateError] = useState("");


  const boxStyle: React.CSSProperties = {
    fontSize: "13px",
    border: "1px solid #d9d9d9",
    padding: "2px 12px",
    borderRadius: "20px",
    backgroundColor: "#f9f9f9",
    textAlign: "center",
  };

  const timerStyle: React.CSSProperties = {
    fontSize: "13px",
    padding: "2px 12px",
    borderRadius: "20px",
    backgroundColor: "#f0f5ff", // light blue
    color: "#3b82f6", // blue text
    fontWeight: "bold",
    textAlign: "center",
  };

  const fontHeaderStyle: React.CSSProperties = {
    fontSize: "14.5px",
    fontWeight: "bold",
    paddingBottom: "6px"
  };

  const fontContentStyle: React.CSSProperties = {
    fontSize: "13px",
    color: "#5e5e5e"
  };




  getTrackingById(projectDetails.tracking).then((trackingLabel) => {
    const label = trackingLabel.data.trackingType;
    setTrackingLabel(label);
  });

  useEffect(() => {
    const fetchNextGate = async () => {
      if (!projectDetails?.id) return;

      console.log("Fetching Next Gate for project:", projectDetails?.id);

      try {
        const response = await getNextGateByProjectProjectId(Number(projectDetails.id));

        const { status } = response.data;

        if (status === 200) {
          setNextGate("Project completed");
        }
        else if (status === 500 || status === 400) {
          setNextGate("Next gate doesn't exist");
        } else {
          setNextGate(response.data);
        }

      } catch (error) {
        console.error("Unhandled error:", error);
        setNextGate("Couldn't fetch gates");
        setNextGateError("Unexpected error occurred");
      }

    };


    fetchNextGate();
  }, [projectDetails?.id]);


  console.log("Next gate error:", nextGateError);
  console.log("Next gate value:", nextGate);



  if (!projectDetails || !projectDetails.launchDate || !projectDetails.passageDate) {
    return <div>Invalid project details</div>;
  }




  return (
    <div >
      <ComponentCard>
        <div style={{ paddingLeft: "2rem", paddingRight: "2rem", display: "flex", justifyContent: "space-between", alignItems: "center", gap: "10px" }}>

          {/* Prochain Gate */}
          <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
            <h1 style={{ fontSize: "14px", fontWeight: "bold" }}>Next Gate :</h1>
            <div style={boxStyle}>
              <p>{nextGate}</p>
            </div>
          </div>

          {/* Date de passage */}
          <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
            <h1 style={{ fontSize: "14px", fontWeight: "bold" }}>Passage Date:</h1>
            <div style={boxStyle}>
              <p>{projectDetails.passageDate.toLocaleString("fr-FR", { dateStyle: "short", timeStyle: "short" })}</p>
            </div>
          </div>

          {/* Date de lancement TTM */}
          <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
            <h1 style={{ fontSize: "14px", fontWeight: "bold" }}>TTM Launch Date:</h1>
            <div style={boxStyle}>
              <p>{projectDetails.launchDate.toLocaleString("fr-FR", { dateStyle: "short", timeStyle: "short" })}</p>
            </div>
          </div>

          {/* Timer */}
          <div style={timerStyle}>
            <p>

              {Math.floor(((projectDetails.passageDate.getTime() - projectDetails.launchDate.getTime()) / (1000 * 60 * 60 * 24)))}d{" "}
              {Math.floor(((projectDetails.passageDate.getTime() - projectDetails.launchDate.getTime()) % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))}h{" "}
              {Math.floor(((projectDetails.passageDate.getTime() - projectDetails.launchDate.getTime()) % (1000 * 60 * 60)) / (1000 * 60))}m{" "}
              {Math.floor(((projectDetails.passageDate.getTime() - projectDetails.launchDate.getTime()) % (1000 * 60)) / 1000)}s

            </p>
          </div>

        </div>
      </ComponentCard>



      <div style={{ position: "relative", top: "16px", paddingBottom: "9rem" }}>
        <ComponentCard>
          <div style={{ padding: "16px 0 0 70px", }}>

            {/* Project title */}

            <div >
              <Image
                src="/images/icons/paper/paper.png"
                alt="recap"
                width={100}
                height={100}
                className="w-6 h-5"
                style={{ position: "absolute", top: "46px", left: "40px" }}
              />
              <h1 style={fontHeaderStyle}>Subject </h1>
              <h1 style={fontContentStyle}>{projectDetails.title} </h1>

            </div>


            {/* Line bar */}

            <div style={{
              backgroundColor: "#e5e5e5",
              width: "102%",
              height: "0.3px",
              position: "relative",
              top: "40px",
              right: "3rem",
            }}>

            </div>


            {/* Project Description */}

            <div style={{ paddingTop: "4rem", paddingRight: "2rem" }}>
              <Image
                src="/images/icons/paragraph/barre-de-menu.png"
                alt="project description icon"
                width={100}
                height={100}
                className="w-6 h-5"
                style={{ position: "absolute", top: "9.9rem", left: "2.8rem" }}
              />
              <h1 style={fontHeaderStyle}>Description </h1>
              <h1 style={fontContentStyle}>{projectDetails.description} </h1>

            </div>


            {/* Line bar */}

            <div style={{
              backgroundColor: "#e5e5e5",
              width: "102%",
              height: "0.3px",
              position: "relative",
              top: "40px",
              right: "3rem",
            }}>

            </div>


            {/* Owner | Department | Tracking */}

            <div className="flex justify-between gap-28 pt-16">
              {/* Owner */}
              <div className="flex-1">
                <div className="flex items-start gap-6 mb-2">
                  <Image src="/images/icons/users/or-icon.png" alt="Owner icon" width={40} height={40} />
                  <div>
                    <p style={fontHeaderStyle}>Owner</p>
                    <p style={fontContentStyle}>{projectDetails.owner}</p>
                  </div>
                </div>
              </div>

              {/* Direction */}
              <div className="flex-1">
                <div className="flex items-start gap-6 mb-2">
                  <Image src="/images/icons/users/doble-icon.png" alt="Direction icon" width={40} height={40} />
                  <div>
                    <p style={fontHeaderStyle}>Direction</p>
                    <p style={fontContentStyle}>{projectDetails.departments.join(", ")}</p>
                  </div>
                </div>
              </div>

              {/* Tracking */}
              <div className="flex-1">
                <div className="flex items-start gap-6 mb-2">
                  <Image className="mt-1" src="/images/icons/tracking-icons/hierar.png" alt="Tracking icon" width={27} height={27} />
                  <div>
                    <p style={fontHeaderStyle}>Tracking</p>
                    <p style={fontContentStyle}>{trackingLabel.toLowerCase()}</p>
                  </div>
                </div>
              </div>
            </div>



            {/* Line bar */}

            <div style={{
              backgroundColor: "#e5e5e5",
              width: "102%",
              height: "0.3px",
              position: "relative",
              top: "40px",
              right: "3rem",
            }}>

            </div>


            {/* Owner | Kickof Marché | Kickoff Moa */}

            <div className="flex justify-between gap-28 pt-16 pb-12">
              {/* Owner MOA */}
              <div className="flex-1">
                <div className="flex items-start gap-6 mb-2">
                  <Image src="/images/icons/users/gre-icon.png" alt="MOA icon" width={40} height={40} />
                  <div>
                    <p style={fontHeaderStyle}>Owner MOA</p>
                    <p style={fontContentStyle}>{projectDetails.ownerMoa
                      ? projectDetails.ownerMoa.join(", ")
                      : "N/A"}</p>
                  </div>
                </div>
              </div>

              {/* Kickoff marché */}
              <div className="flex-1">
                <div className="flex items-start gap-6 mb-2">
                  <Image src="/images/icons/date/date.png" alt="Kickoff marché icon" width={27} height={27} />
                  <div>
                    <p style={fontHeaderStyle}>Kickoff market</p>
                    <p style={fontContentStyle}>
                      {projectDetails.launchDate.toLocaleString("fr-FR", { dateStyle: "short", timeStyle: "short" })}
                    </p>
                  </div>
                </div>
              </div>

              {/* Kickoff MOA */}
              <div className="flex-1">
                <div className="flex items-start gap-6 mb-2">
                  <Image src="/images/icons/date/date.png" alt="Kickoff MOA icon" width={27} height={27} />
                  <div>
                    <p style={fontHeaderStyle}>Kickoff MOA</p>
                    <p style={fontContentStyle}>
                      {projectDetails.passageDate.toLocaleString("fr-FR", { dateStyle: "short", timeStyle: "short" })}
                    </p>
                  </div>
                </div>
              </div>
            </div>



          </div>







        </ComponentCard>
      </div>




    </div>

  );
}