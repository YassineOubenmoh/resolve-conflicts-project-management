<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>🔓 New Gate Unlocked – Your Project is Advancing</title>
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f0f4f8;
            color: #333;
            padding: 30px;
        }

        .container {
            background-color: #fff;
            border-radius: 10px;
            padding: 30px 40px;
            max-width: 750px;
            margin: auto;
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
        }

        h2 {
            color: #ad2184;
            margin-bottom: 20px;
        }

        h3 {
            color: #4a5568;
            margin-top: 30px;
            border-bottom: 2px solid #edf2f7;
            padding-bottom: 5px;
        }

        p {
            line-height: 1.6;
        }

        .info-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        .info-table td {
            padding: 8px 0;
            vertical-align: top;
        }

        .info-table .label {
            font-weight: bold;
            width: 180px;
            color: #555;
        }

        ul {
            padding-left: 20px;
            margin: 0;
        }

        li {
            margin-bottom: 5px;
        }

        .footer {
            margin-top: 40px;
            font-size: 0.9em;
            color: #999;
            text-align: center;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Hello ${firstName} ${lastName},</h2>

    <p>
        We’re pleased to inform you that your project <strong>${projectName}</strong> from the <strong>${department}</strong> department
        has successfully transitioned from <strong>${passedGate}</strong> to <strong>${futureGate}</strong>.
    </p>

    <h3>🚀 Gate Transition Summary</h3>
    <table class="info-table">
        <tr>
            <td class="label">Project Name:</td>
            <td>${projectName}</td>
        </tr>
        <tr>
            <td class="label">Department:</td>
            <td>${department}</td>
        </tr>
        <tr>
            <td class="label">From Gate:</td>
            <td>${passedGate}</td>
        </tr>
        <tr>
            <td class="label">To Gate:</td>
            <td>${futureGate}</td>
        </tr>
        <tr>
            <td class="label">Passing Date:</td>
            <td>${passingDate}</td>
        </tr>
    </table>

    <h3>📝 Recap of Passed Gate</h3>
    <table class="info-table">
        <tr>
            <td class="label">Information:</td>
            <td>
                <ul>
                    <#list information as info>
                        <li>${info}</li>
                    </#list>
                </ul>
            </td>
        </tr>
        <tr>
            <td class="label">Actions:</td>
            <td>
                <ul>
                    <#list actions as act>
                        <li>${act}</li>
                    </#list>
                </ul>
            </td>
        </tr>
        <tr>
            <td class="label">Decisions:</td>
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
        If you have any questions or concerns regarding this transition, please contact the PMO office.
    </p>

    <div class="footer">
        <p>This is an automated message. Please do not reply.</p>
    </div>
</div>
</body>
</html>
