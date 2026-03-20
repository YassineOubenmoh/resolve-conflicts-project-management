<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>⛔ Gate Progress Suspension</title>
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
    <h2>Bonjour ${lastName} ${firstName},</h2>

    <p>
        La gate <strong class="highlight">${gate}</strong>
        du projet <strong class="highlight">${projectName}</strong> est suspendue pour le moment
    </p>

    <p>
        Si vous avez des questions ou avez besoin d’aide supplémentaire, n’hésitez pas à contacter l’équipe PMO.
    </p>

    <div class="footer">
        <p>Il s’agit d’un e-mail automatisé – veuillez ne pas répondre directement.</p>
    </div>
</div>
</body>
</html>
