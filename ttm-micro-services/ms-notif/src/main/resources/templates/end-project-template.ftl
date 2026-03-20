<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>✅ Project Completed!</title>
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
            border-bottom: 2px solid #ffeffa;
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
            color: #2c5282;
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

        .project-summary {
            background-color: #ffeffa;
            border-left: 5px solid #ad2184;
            padding: 20px;
            border-radius: 8px;
            margin: 30px 0;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Hello ${firstName} ${lastName},</h2>

    <div class="project-summary">
        <p>
            🎉 Congratulations! We are pleased to inform you that your project
            <span class="highlight">${projectName}</span> from the
            <span class="highlight">${department}</span> department has successfully reached its completion.
        </p>
    </div>

    <h3>📅 Project Completion Date: ${passingDate}</h3>
    <h3>⏱️ TTM Period: ${daysTtm} days</h3>

    <h3>📝 Recap of Passed Gate</h3>
    <table class="info-table">
        <tr>
            <td class="label">Gate</td>
            <td>${passedGate}</td>
        </tr>
        <tr>
            <td class="label">Information</td>
            <td>
                <ul>
                    <#list information as info>
                        <li>${info}</li>
                    </#list>
                </ul>
            </td>
        </tr>
        <tr>
            <td class="label">Actions</td>
            <td>
                <ul>
                    <#list actions as act>
                        <li>${act}</li>
                    </#list>
                </ul>
            </td>
        </tr>
        <tr>
            <td class="label">Decisions</td>
            <td>
                <ul>
                    <#list decisions as decision>
                        <li>${decision}</li>
                    </#list>
                </ul>
            </td>
        </tr>
    </table>

    <p>
        If you have any questions or concerns regarding this transition, please do not hesitate to contact the PMO office.
    </p>

    <div class="footer">
        <p>This is an automated message – please do not reply.</p>
    </div>
</div>
</body>
</html>
