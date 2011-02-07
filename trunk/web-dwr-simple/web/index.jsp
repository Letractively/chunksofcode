<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %> 
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>

<html:html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Simle Anwendungen f&uuml;r DWR</title>
        <html:base/>
    </head>
    <body style="background-color: white">
        <h3>Simle Anwendungen f&uuml;r DWR</h3>
        <html:link href="./showServerTime.jsp">
            serverzeit anzeigen
        </html:link>
        <br/>
        <html:link href="./sayHello.jsp">
            benutzer begr&uuml;&szlig;en
        </html:link>
    </body>
</html:html>
