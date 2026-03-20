<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>🚀 New Feedback on Impact</title>
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f7f8fc;
            color: #333;
            padding: 40px;
        }

        .container {
            background-color: #ffffff;
            border-radius: 16px;
            padding: 50px;
            max-width: 750px;
            margin: auto;
            box-shadow: 0 12px 35px rgba(0, 0, 0, 0.08);
        }

        h2, h3 {
            color: #b2186b;
            font-weight: 700;
            margin-top: 0;
        }

        h2 {
            font-size: 28px;
            margin-bottom: 20px;
        }

        h3 {
            font-size: 20px;
            margin-top: 40px;
            padding-bottom: 8px;
            border-bottom: 2px solid #f0cce5;
        }

        p {
            font-size: 16px;
            color: #4a5568;
            line-height: 1.7;
            margin: 20px 0;
        }

        .info-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        .info-table td {
            padding: 12px 0;
            vertical-align: top;
        }

        .info-table .label {
            font-weight: bold;
            width: 220px;
            color: #b2186b;
            font-size: 15px;
        }

        .highlight {
            color: #b2186b;
            font-weight: 600;
        }

        .footer {
            margin-top: 50px;
            font-size: 0.9em;
            color: #a0aec0;
            text-align: center;
        }

        .summary-box {
            background-color: #fff3f9;
            border-left: 6px solid #b2186b;
            padding: 20px;
            border-radius: 8px;
            margin: 30px 0;
        }

        .download-button {
            display: inline-block;
            margin-top: 10px;
            padding: 12px 25px;
            background-color: #b2186b;
            color: #ffffff !important;
            font-weight: bold;
            border-radius: 6px;
            text-decoration: none;
            font-size: 14px;
            transition: background-color 0.3s;
        }

        .download-button:hover {
            background-color: #da1f84;
        }

        .img {
            text-align: center;
            margin-bottom: 40px;
        }

        .img img {
            max-width: 160px;
            height: auto;
        }

        ul {
            padding-left: 20px;
            margin: 0;
            color: #4a5568;
        }

        li {
            margin-bottom: 6px;
        }

        .action {
            color: #b2186b;
        }

        .validation {
            font-size: 18px;
            margin-top: 10px;
        }
    </style>
</head>
<body>


<div class="container">
    <h2 class="action">🔔 ${responseToActionLabel}</h2>

    <div class="summary-box">
        <p>
            Bonjour, je souhaite <strong>partager</strong> avec vous mon retour concernant votre impact toujours dans le cadre du projet
            <span class="highlight">${projectName}</span>.
        </p>
    </div>

    <p class="validation">${validationStatus}</p>

    <p>${justificationStatus}</p>

    <h3>📎 Informations complémentaires</h3>
    <table class="info-table">
        <tr>
            <td class="label">Document du retour</td>
            <td>
                <#if responseDocument??>
                    <a class="download-button" href="http://localhost:8080/action/download/${responseDocument}" target="_blank">📄 Télécharger le document</a>
                <#else>
                    Document non disponible
                </#if>
            </td>
        </tr>
        <tr>
            <td class="label">Validé par</td>
            <td>${validatedBy}</td>
        </tr>
    </table>

    <p>
        Si vous avez des questions ou besoin de précisions supplémentaires, vous pouvez contacter l'équipe PMO ou directement l'expéditeur.
    </p>

    <div class="footer">
        <p>📩 Ceci est un message automatique — merci de ne pas y répondre directement.</p>
    </div>
</div>
</body>
</html>
