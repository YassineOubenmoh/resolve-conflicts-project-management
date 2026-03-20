<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>📢 New Project </title>
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f8f9fc;
            color: #333;
            padding: 30px;
        }

        .container {
            background-color: #ffffff;
            border-radius: 12px;
            padding: 40px 50px;
            max-width: 800px;
            margin: auto;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.06);
        }

        h2 {
            color: #333333;
            font-size: 24px;
            margin-bottom: 20px;
        }

        p {
            color: #555555;
            font-size: 16px;
            line-height: 1.6;
        }

        strong.highlight {
            color: #ad2184;
        }

        h3 {
            color: #4a4a4a;
            margin-top: 35px;
            font-size: 18px;
            border-bottom: 2px solid #e2e8f0;
            padding-bottom: 8px;
        }

        table.info-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }

        .info-table td {
            padding: 10px 5px;
            vertical-align: top;
        }

        .info-table .label {
            font-weight: bold;
            width: 200px;
            color: #444;
        }

        ul {
            margin: 0;
            padding-left: 20px;
        }

        li {
            margin-bottom: 6px;
        }

        .footer {
            margin-top: 50px;
            font-size: 0.9em;
            color: #888;
            text-align: center;
        }

        .highlight-block {
            background-color: #ffeffa;
            border-left: 5px solid #881568;
            padding: 15px 20px;
            border-radius: 6px;
            margin-top: 25px;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Hello ${lastName} ${firstName},</h2>

    <p>
        The project <strong class="highlight">${projectName}</strong>
        has been assigned to the
        <strong class="highlight">${department}</strong>
        department. Below you’ll find the full details of your project and its context.
    </p>

    <div class="highlight-block">
        <h3>📋 Project Overview</h3>
        <table class="info-table">
            <tr>
                <td class="label">Project Owner:</td>
                <td>${ownerName}</td>
            </tr>
            <tr>
                <td class="label">Project Type:</td>
                <td>${projectType}</td>
            </tr>
            <tr>
                <td class="label">Market Type:</td>
                <td>${marketType}</td>
            </tr>
            <tr>
                <td class="label">Confidentialité:</td>
                <td>${confidential}</td>
            </tr>
            <tr>
                <td class="label">Date Start TTM:</td>
                <td>${dateStartTtm}</td>
            </tr>
        </table>
    </div>

    <h3>📝 Additional Details</h3>
    <table class="info-table">
        <tr>
            <td class="label">TTM Committee Subcategory:</td>
            <td>${ttmComitteeSubCategory}</td>
        </tr>
        <tr>
            <td class="label">Subcategory Commercial Codir:</td>
            <td>${subcategoryCommercialCodir}</td>
        </tr>
        <tr>
            <td class="label">Description:</td>
            <td>${description}</td>
        </tr>
    </table>

    <p>
        If you have any questions or need further assistance, feel free to reach out to the PMO team.
    </p>

    <div class="footer">
        <p>This is an automated email – please do not reply directly.</p>
    </div>
</div>
</body>
</html>
