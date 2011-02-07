<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!--Document   : showServerTime.jsp
    Created on : 24.12.2008, 18:18:52
    Author     : andre -->

<%String ctxtRoot = config.getServletContext().getContextPath();%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

        <title>DWR Demo: showServerTime.jsp</title>        

        <script type='text/javascript' src='<%=ctxtRoot%>/dwr/interface/HelloDWR.js'></script>
        <script type='text/javascript' src='<%=ctxtRoot%>/dwr/engine.js'></script>
        <script type='text/javascript' src='<%=ctxtRoot%>/dwr/util.js'></script>
    </head>

    <body>
        <h1>Zeige die momentane Zeit am Server via AJAX</h1>
        <input type="button" onclick="showServerTime()" value="zeit aktualisieren"/>
        <div id="serverTime">serverzeit</div>

        <script type="text/javascript">
            /* shows the current server time */
            function showServerTime() {
                HelloDWR.getServerTime(sayTimeCallback);
            }
            function sayTimeCallback(data) {
                dwr.util.setValue('serverTime', data);
            }
        </script>

    </body>

</html>
