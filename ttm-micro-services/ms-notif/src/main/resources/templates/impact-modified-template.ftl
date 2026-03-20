<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>🔄 Impact Updated</title>
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f0f2f5;
            color: #333;
            padding: 40px;
        }

        .container {
            background-color: #ffffff;
            border-radius: 14px;
            padding: 50px;
            max-width: 780px;
            margin: auto;
            box-shadow: 0 12px 28px rgba(0, 0, 0, 0.08);
        }

        h2 {
            color: #1a202c;
            font-size: 26px;
            margin-bottom: 20px;
        }

        p {
            font-size: 16px;
            color: #4a5568;
            line-height: 1.7;
        }

        h3 {
            color: #2d3748;
            font-size: 18px;
            margin-top: 40px;
            margin-bottom: 10px;
            padding-bottom: 8px;
            border-bottom: 2px solid #e2e8f0;
        }

        .info-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }

        .info-table td {
            padding: 10px 0;
            vertical-align: top;
        }

        .info-table .label {
            font-weight: 600;
            width: 200px;
            color: #881568;
            font-size: 15px;
        }

        ul {
            padding-left: 20px;
            margin: 0;
            color: #4a5568;
        }

        li {
            margin-bottom: 6px;
        }

        .highlight {
            color: #881568;
            font-weight: 600;
        }

        .footer {
            margin-top: 50px;
            font-size: 0.9em;
            color: #a0aec0;
            text-align: center;
        }

        .summary-box {
            background-color: #ffeffa;
            border-left: 5px solid #ad2184;
            padding: 20px;
            border-radius: 8px;
            margin: 30px 0;
        }

        .download-button {
            display: inline-block;
            margin-top: 10px;
            padding: 12px 25px;
            background-color: #881568;
            color: #ffffff !important;
            font-weight: bold;
            border-radius: 6px;
            text-decoration: none;
            font-size: 14px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        .download-button:hover {
            background-color: #ad2184;
        }

        .img {
            text-align: center;
            margin-bottom: 30px;
        }

        .img img {
            max-width: 180px;
            height: auto;
        }

        .action{
            color: #ad2184;
        }


    </style>
</head>
<body>


<div class="container">
    <h2 class="action">${actionLabel}</h2>
    <h2>Hello ${firstName} ${lastName},</h2>

    <div class="summary-box">
        <p>
            The <strong>impact</strong> modification has just been registered for the required action
            <span class="highlight">${requiredActions}</span> within the project
            <span class="highlight">${projectName}</span>

        </p>
        <p>
            This notification has been shared by <span class="highlight">${impactSenderEmail}</span>. Please find the details below.
        </p>
    </div>

    <h3>📌 Impact Details</h3>
    <table class="info-table">
        <tr>
            <td class="label">Created By</td>
            <td>${actionCreatedBy}</td>
        </tr>
        <tr>
            <td class="label">Related Document</td>
            <td>
                <#if actionDocument??>
                    <a class="download-button" href="http://localhost:8080/action/download/${actionDocument}" target="_blank">📄 Download Document</a>
                <#else>
                    Not available
                </#if>
            </td>
        </tr>
        <tr>
            <td class="label">Comments</td>
            <td>
                <#if comments?has_content>
                    <ul>
                        <#list comments as comment>
                            <li>${comment}</li>
                        </#list>
                    </ul>
                <#else>
                    No comments provided.
                </#if>
            </td>
        </tr>
    </table>

    <p>
        If you have any questions or need further clarification, feel free to contact the PMO team or reach out directly to the sender.
    </p>

    <div class="footer">
        <p>This is an automated message — please do not reply directly to this email.</p>
    </div>
</div>
</body>
</html>
