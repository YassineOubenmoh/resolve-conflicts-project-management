<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Mission Activation: Gate ${gate} Assigned</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #eef3f7;
            color: #2c3e50;
        }

        .container {
            max-width: 750px;
            margin: 50px auto;
            background-color: #ffffff;
            padding: 40px;
            border-radius: 16px;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
            box-sizing: border-box;
            border: 1px solid #dbe1e8;
        }

        h2 {
            font-size: 28px;
            color: #ad2184;
            text-align: center;
            margin-bottom: 30px;
            border-bottom: 2px solid #ad2184;
            padding-bottom: 10px;
        }

        p {
            font-size: 16px;
            line-height: 1.7;
            margin: 18px 0;
        }

        .highlight {
            font-weight: 600;
            color: #881568;
        }

        .info-table {
            width: 100%;
            border-collapse: collapse;
            margin: 30px 0;
        }

        .info-table th,
        .info-table td {
            padding: 14px 16px;
            text-align: left;
            border: 1px solid #d1d9e6;
        }

        .info-table th {
            background-color: #ffeffa;
            color: #881568;
            font-size: 15px;
        }

        .info-table td {
            background-color: #fafcff;
            font-size: 15px;
        }

        ul {
            padding-left: 20px;
            margin: 20px 0;
        }

        li {
            font-size: 15px;
            margin-bottom: 8px;
        }

        .footer {
            margin-top: 40px;
            text-align: center;
            font-size: 13px;
            color: #6c757d;
            border-top: 1px solid #e0e0e0;
            padding-top: 20px;
        }

        .footer small {
            display: block;
            margin-top: 6px;
        }

        .divider {
            height: 1px;
            background-color: #e0e0e0;
            margin: 40px 0 30px;
        }
    </style>
</head>
<body>

<div class="container">
    <h2>Gate Affectation Notification</h2>

    <p>Dear <span class="highlight">${firstName} ${lastName}</span>,</p>

    <p>
        We are pleased to inform you that a gate has been officially assigned to your department
        <span class="highlight">${department}</span> for the project:
        <span class="highlight">${projectName}</span>.
    </p>

    <div class="divider"></div>

    <table class="info-table">
        <tr>
            <th>Project Name</th>
            <td>${projectName}</td>
        </tr>
        <tr>
            <th>Department</th>
            <td>${department}</td>
        </tr>
        <tr>
            <th>Assigned Gate</th>
            <td><strong>${gate}</strong></td>
        </tr>
    </table>

    <h3 style="color:#ad2184; margin-top:40px;">Required Actions</h3>
    <p>Please make sure to complete the following tasks:</p>
    <ul>
        <#list requiredActions as action>
            <li>${action}</li>
        </#list>
    </ul>

    <p>If you have any questions or require further clarification, feel free to reach out to the administration team at your earliest convenience.</p>

    <div class="footer">
        <p>Thank you for your attention and cooperation.</p>
        <small>This is an automated message — please do not reply directly to this email.</small>
    </div>
</div>

</body>
</html>
