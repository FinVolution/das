<!DOCTYPE html>
<head>
    <meta charset="utf8">
    <title>das-console-app</title>
    <link href="pages/images/das.ico" rel="icon" type="img/x-ico"/>
    <link href="pages/images/das.ico" rel="bookmark"/>
</head>
<header>
    <script src="https://code.jquery.com/jquery-3.2.1.min.js"
            integrity="sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4="
            crossorigin="anonymous"></script>
    <style type="text/css">
        .box{
            min-width:1800px;
            overflow-x: auto;
        }
    </style>
</header>
<body class="box">
<div id="root">
</div>
<script>
    window.DASENV = {
        user: JSON.parse('${Request['user']}'),
        isAdmin:${Request["isAdmin"]},
        isDasLogin:${Request["isDasLogin"]},
        dasSyncTarget: '${Request["dasSyncTarget"]}',
        configName: '${Request["configName"]}',
        securityName: '${Request["securityName"]}',
        isDev: ${Request["isDev"]?string('true','false')}
    }
</script>
<#if Request['isLocal'] && Request['isDev']>
    <script src="http://127.0.0.1:3005/common.js"></script>
    <script src="http://127.0.0.1:3005/index.js"></script>
<#else>
    <link rel="stylesheet" type="text/css" href="../pages/dist/index.css"/>
    <script src="../pages/dist/common.js"></script>
    <script src="../pages/dist/index.js?t=20191203"></script>
</#if>
</body>
</html>