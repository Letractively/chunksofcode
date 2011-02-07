<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>

<html:html xhtml="true">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>/pages/multibox/multiboxResults.jsp</title>
    </head>
    <body>
        <h1>&lt;html:multibox&gt; test page - results</h1>
        <p>
            you selected following members one page ago:<br/>
            they were also written to server logfile during MultiboxProcessAction
        </p>
        <table style="background-color:silver">
            <tr>
                <th>Members:</th>
            </tr>
            <%-- iterates over the array from getPersons() of the
                          action bean called multiboxForm--%>
            <logic:iterate name="multiboxForm" property="persons" id="p">
                <tr>
                    <td><bean:write name="p" /></td>
                </tr>
            </logic:iterate>
        </table>
        <p>
            <html:link forward="welcome" >home</html:link><br/>
            <html:link action="/prepareMultibox" >again</html:link>
        </p>
    </body>
</html:html>
