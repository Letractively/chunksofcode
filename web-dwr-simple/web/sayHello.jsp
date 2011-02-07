<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!--Document   : sayHello.jsp
    Created on : 24.12.2008, 13:31:15
    Author     : andre -->

<%String ctxtRoot = config.getServletContext().getContextPath();%>

<html>
    <head>
        <title>DWR Demo: sayHello.jsp</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>


        <!-- To use this class in your javascript you will need the following script includes:-->
        <script type='text/javascript' src='<%=ctxtRoot%>/dwr/interface/HelloDWR.js'></script>
        <script type='text/javascript' src='<%=ctxtRoot%>/dwr/engine.js'></script>
        <script type='text/javascript' src='<%=ctxtRoot%>/dwr/util.js'></script>

        <script type="text/javascript">

            /* display a welcome message to user */
            function greetUser() {
                var name = dwr.util.getValue('inputName');
                HelloDWR.sayHello(name, sayHelloCallback);
            }
            function sayHelloCallback(data) {
                dwr.util.setValue('answerDiv', data);
                dwr.util.setValue('inputName', '');
            }
        </script>
    </head>
    <body>
        <h1>Begr&uuml;&szlig;ung via AJAX</h1>
        Bitte Namen eingeben:
        <input id="inputName" type="text"/>
        <input type="button" onclick="greetUser()" value="begruessung"/>
        <div id="answerDiv">begruessungstext</div>
    </body>
</html>
